package org.example.service_invalid;

import com.google.gson.Gson;
import org.example.model.Job;
import org.example.model.ResponseWrapper;
import org.example.model.UrlsDTO;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ActionInitiator {
    private static final String URL = "https://api.peviitor.ro/v1/search/?page=";
    private Integer tick = 0;
    private HttpClient httpClient;
    private static final Logger logger = Logger.getLogger(ActionInitiator.class.getName());

    public ActionInitiator() {
        httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    }

    private void setTick(Integer recordsNumber){
        if(recordsNumber % 12 == 0){
            this.tick = recordsNumber/12;
        } else {
            this.tick = recordsNumber/12 + 1;
        }
    }

    public ResponseWrapper onePageJobsSearch() {
        try {
            Gson gson = new Gson();
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(new URI(URL + tick.toString()))
                    .build();
            HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
            ResponseWrapper responseWrapper = gson.fromJson(getResponse.body(), ResponseWrapper.class);
            if(tick == 0){
                setTick(responseWrapper.getResponse().getNumFound());
            }
            logger.info("Completed page " + tick.toString() + " request.");
            tick--;
            return responseWrapper;
        }catch (URISyntaxException | NullPointerException e){
            logger.severe("Error: " + e.getMessage() + "\n" + "URI Issue tried to create URI with string: " + URL + tick.toString());
        }catch (IOException | InterruptedException e){
            tick++;
            logger.warning("Could not send pe viitor request. Message: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
        return null;
    }

    public List<String> threadInitiator(){
        ArrayList<String> urls = new ArrayList<>();
        List<String> markedForDeletion = new ArrayList<>();
        int iterations = 0;
        boolean keepLooping = true;
        do{
            ResponseWrapper res = onePageJobsSearch();
            iterations++;
            List<Job> docs = res.getResponse().getDocs();
            if((docs == null || (docs.isEmpty()) && urls.isEmpty()) || tick == 0 ){
                keepLooping=false;
            }
            if (docs!=null && !docs.isEmpty()){
                urls.addAll(res.getResponse().getDocs().stream().map(job -> job.getUrl()).collect(Collectors.toList()));
            }
            if(iterations % 200 == 0 || (!urls.isEmpty() && (docs == null || docs.isEmpty()))){
                markedForDeletion.addAll(spawnLinkCheckers(urls));
                System.out.println(markedForDeletion);
                trySendDeleteRequests(markedForDeletion);
                markedForDeletion.clear();
                urls.clear();
            }
        }while (keepLooping && tick != 0);
        return markedForDeletion;
    }

    public void trySendDeleteRequests(List<String> urlsList){
        try{
            for (int i = 0; i < urlsList.size()-1; i+=100) {
                if(i+100 > urlsList.size()){
                    UrlsDTO urlsDTO = new UrlsDTO(urlsList.subList(i, urlsList.size()));
                    sendDeleteRequest(urlsDTO);
                } else {
                    UrlsDTO urlsDTO = new UrlsDTO(urlsList.subList(i, i+100));
                    sendDeleteRequest(urlsDTO);
                }
            }
        } catch (URISyntaxException e) {
            logger.severe("URISyntaxException from sendDeleteRequest() method " + e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.severe("IOException encountered from the sendDeleteRequest() method " + e.getMessage());
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            logger.severe("InterruptedException thrown from sendDeleteRequest() " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void sendDeleteRequest(UrlsDTO urlsDTO) throws URISyntaxException, IOException, InterruptedException {
        Gson gson = new Gson();
        String requestBody = gson.toJson(urlsDTO, UrlsDTO.class);
        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create("https://api.peviitor.ro/v1/delete/"))
                .method("DELETE", HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> deleteResponse = httpClient.send(httpRequest,HttpResponse.BodyHandlers.ofString());
        if (deleteResponse.statusCode() >= 200 && deleteResponse.statusCode() < 300){
            logger.info("Delete Request Successful!");
        } else if(deleteResponse.statusCode() >= 500){
            logger.warning("Server issue, request did not go through, require further investigation.");
        } else {
            logger.warning("Possible bad request issue, status between 300 and 499, debug necessary!");
        }
    }

    private List<String> listMaker(int i, int j, ArrayList<String> array){
        return new ArrayList<>(array.subList(i,j));
    }

    public List<String> spawnLinkCheckers(ArrayList<String> jobs){
        ArrayList<Thread> threads = new ArrayList<>();
        ArrayList<RequestMaker> requestMakers = new ArrayList<>();
        List<String> deletionURLs = new ArrayList<>();
        int threadNumber = jobs.size()/160;
        if((!jobs.isEmpty() && threadNumber == 0) || (jobs.size() % 160 != 0 )){
            threadNumber++;
        }
        if( threadNumber != 0 ){
            for (int i = 0; i < threadNumber; i++) {
                System.out.println("Starting threads total: " + threadNumber);
                int j = i*160;
                if(j < jobs.size() && j+160 > jobs.size()){
                    requestMakers.add(new RequestMaker(listMaker(j, jobs.size(), jobs), null));
                    threads.add(new Thread(requestMakers.get(i)));
                } else {
                    requestMakers.add(new RequestMaker(listMaker(j, j+160, jobs), null));
                    threads.add(new Thread(requestMakers.get(i)));
                }
                threads.get(i).start();
            }
            for (int i = 0; i < threads.size(); i++) {
                try {
                    threads.get(i).join();
                } catch (InterruptedException e) {
                    threads.get(i).interrupt();
                    System.out.println("thread " + i + "failed: " + e.getMessage());
                }
            }
            for (int i = 0; i < threads.size(); i++) {
                System.out.println("Thread " + i + "returned a list of: " + requestMakers.get(i).getRemainingURLs().size());
                deletionURLs.addAll(requestMakers.get(i).getRemainingURLs());
            }
        }
        return deletionURLs;
    }
}
