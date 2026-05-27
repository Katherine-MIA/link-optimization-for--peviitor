package org.example.service;


import okhttp3.CipherSuite;
import okhttp3.ConnectionPool;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import org.example.model.UrlsDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Delegator {
    private final Semaphore spawner = new Semaphore(400);
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final Logger logger = Logger.getLogger(Delegator.class.getName());
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .connectionPool(new ConnectionPool(200, 5, TimeUnit.MINUTES))
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .build();
    private APICaller apiCaller;

    public Delegator() {
        this.apiCaller = new APICaller(client);
    }

    public List<String> callOnePage() {
        List<String> urls = apiCaller.getJobsOnePage();
        CheckerForLinks checkerForLinks = new CheckerForLinks(client,urls);
        checkerForLinks.startCheck();
        return checkerForLinks.getUrls();
    }

    private HashMap<Integer, ArrayList<String>> getFirstTwoHundred(){
        HashMap<Integer, ArrayList<String>> first = new HashMap<>();
        for(int i = 0; i < 200; i++) {
            if(apiCaller.getTick() == 0){
                i = 200;
                continue;
            }
            Integer tick = apiCaller.getTick();
            ArrayList<String> list = apiCaller.getJobsOnePage();
            first.put(tick, list);
        }
        return first;
    }

    public void startSendingRequests(){
        boolean ok = true;
        do {
            HashMap<Integer, ArrayList<String>> links = getFirstTwoHundred();
            if (apiCaller.getTick() % 400 == 0 ){
                ok = false;
            }
            for (Map.Entry<Integer, ArrayList<String>> entry : links.entrySet()) {
                executor.submit(() -> {
                    try {
                        spawner.acquire();
                        logger.info("Thread " + Thread.currentThread().getName() + " started.");
                        CheckerForLinks checkerForLinks = new CheckerForLinks(client, entry.getValue());
                        checkerForLinks.startCheck();
                        UrlsDTO urlsDTO = new UrlsDTO(checkerForLinks.getUrls());
                        logger.info("Send for delete " + urlsDTO.getUrls().size() + " links\n" + urlsDTO.getUrls());
                        //apiCaller.deleteAPICall(urlsDTO);
                    } catch (InterruptedException e) {
                        logger.severe("The thread " + Thread.currentThread().getName() + " has stopped." + e.getMessage());
                        Thread.currentThread().interrupt();
                    } finally {
                        spawner.release();
                    }
                });
            }
        }while(ok);
    }


}
