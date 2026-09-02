package org.example.service_v2;

import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.example.dtos.UrlsDTO;
import org.example.model.JobLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TaskDelegator {
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final Logger logger = LoggerFactory.getLogger(TaskDelegator.class);
    //Timeouts and pool can be adjusted for performance; research on optimisation is recommended unless
    //you're a well-seasoned exceptional engineer, in the ways of networking, web and APIs :)
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .connectionPool(new ConnectionPool(200, 5, TimeUnit.MINUTES))
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .dispatcher(setDispatcher())
            .build();
    private APICaller apiCaller;

    public TaskDelegator() {
        this.apiCaller = new APICaller(client);
    }

    //This is also used for setting .dispatcher(Dispatcher) on the client (OkHttpClient)
    //Also might require research in case optimisations must be made.
    private static Dispatcher setDispatcher(){
        Dispatcher dispatcher = new Dispatcher();
        // Default number on non-custom dispatcher is 64; Changed to 200 for the number of threads
        dispatcher.setMaxRequests(200);
        //May need to be adjusted -> default number on default dispatcher is 5
        //dispatcher.setMaxRequestsPerHost(2);
        return dispatcher;
    }

    public List<JobLink> getOneHundred() {
        List<JobLink> urls = apiCaller.getJobs(100);
        return urls;
    }

    private Queue<JobLink> createLinksConcurrentQueue(List<JobLink> links) {
        Queue<JobLink> urls = new ConcurrentLinkedQueue<>();
        for(JobLink link : links) {
            urls.add(link);
        }
        return urls;
    }

    private Map<String, Queue<JobLink>> constructLinksByHostMap(List<JobLink> links) {
        Map<String, Queue<JobLink>> linksByHost = new ConcurrentHashMap<>();
        for (JobLink link : links) {
            String host = HttpUrl.parse(link.getUrl()).host().toString();
            if(linksByHost.containsKey(host))
                linksByHost.get(host).add(link);
            else {
                Queue<JobLink> linkQueue = new ConcurrentLinkedQueue<>();
                linkQueue.add(link);
                linksByHost.put(host, linkQueue);
            }
        }
        return linksByHost;
    }

    public void startSendingRequests(){
        List<JobLink> list = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            list.addAll(getOneHundred());
        }
        if(list.isEmpty()) return;
        Map<String, Queue<JobLink>> linksByHost = constructLinksByHostMap(list);
        linksByHost.forEach((k,v) -> {
            executor.submit(() -> {
                try {
                    logger.info("Thread " + Thread.currentThread().getName() + " started.");
                    LinkValidator linkValidator = new LinkValidator(client, v);
                    linkValidator.startCheck();
                    UrlsDTO urlsDTO = new UrlsDTO(v.stream()
                            .map(l -> l.getUrl())
                            .collect(Collectors.toList()));
                    logger.info("Send for delete " + urlsDTO.getUrls().size() + " links\n" + urlsDTO.getUrls());
                    //apiCaller.deleteAPICall(urlsDTO);
                } catch (InterruptedException ex) {
                    logger.error("The thread " + Thread.currentThread().getName() + " has stopped." + ex.getMessage());
                    Thread.currentThread().interrupt();
                }
            });
        });
    }
}
