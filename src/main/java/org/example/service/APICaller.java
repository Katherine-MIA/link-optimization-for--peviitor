package org.example.service;


import com.google.gson.Gson;
import jdk.jfr.ContentType;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.example.model.Job;
import org.example.model.ResponseWrapper;
import org.example.model.UrlsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class APICaller {
    private static final String SEARCH_JOB_URL = "https://api.peviitor.ro/v1/search/?page=";
    private static final String DELETE_JOBS_URL = "https://api.peviitor.ro/v1/delete/";
    private OkHttpClient okHttpClient;
    private Integer tick;
    private Logger logger = LoggerFactory.getLogger(APICaller.class);

    public APICaller(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
        this.tick = 0;
    }

    private void setTick(Integer numFound){
        if(numFound % 12 == 0){
            this.tick = numFound/12;
        } else {
            this.tick = numFound/12 + 1;
        }
    }

    public Integer getTick(){
        return this.tick;
    }

    public ArrayList<String> getJobsOnePage() {
        ArrayList<String> jobUrls = new ArrayList<>();
        Request request = new Request.Builder()
                .url(SEARCH_JOB_URL + tick)
                .get()
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if(response.code() == 200 && response.body() != null) {
                Gson gson = new Gson();
                String body = response.body().string();
                ResponseWrapper responseWrapper = gson.fromJson(body, ResponseWrapper.class);
                if(tick == 0){
                    setTick(responseWrapper.getResponse().getNumFound());
                }
                jobUrls = responseWrapper.getResponse()
                        .getDocs()
                        .stream()
                        .map(Job::getUrl)
                        .collect(Collectors.toCollection(ArrayList :: new ));
            }
            logger.trace("Api call for page " + tick + " returned: " + response.code() + "\n");
        } catch (IOException e) {
            logger.error("Request failed for API page " + tick + ": " + e.getMessage());
        }
        tick--;
        return jobUrls;
    }

    public void deleteAPICall(UrlsDTO urls) {
        if(urls.getUrls() == null || urls.getUrls().isEmpty())
            return;
        Gson gson = new Gson();
        String stringBody = gson.toJson(urls, UrlsDTO.class);
        RequestBody body = RequestBody.create(stringBody, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(DELETE_JOBS_URL)
                .delete(body)
                .build();
        try(Response response = okHttpClient.newCall(request).execute()){
            logger.trace("Delete was successful for " + urls.getUrls().size() + "links. Code: " + response.code() + "\n");
        } catch (IOException e) {
            logger.error("Could not delete: " + e.getMessage() , e);
        }
    }
}
