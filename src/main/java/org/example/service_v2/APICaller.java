package org.example.service_v2;

import com.google.gson.Gson;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.example.dtos.UpdateDTO;
import org.example.dtos.UrlsDTO;
import org.example.model.JobLink;
import org.example.model.WrapperJobLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class APICaller {
    private static final String SEARCH_JOB_URL = "https://api.peviitor.ro/v1/validate/?index=";
    private static final String UPDATE_JOBS_URL = "https://api.peviitor.ro/v1/update/";
    private static final String DELETE_JOBS_URL = "https://api.peviitor.ro/v1/delete/";
    private OkHttpClient okHttpClient;
    private final Logger logger = LoggerFactory.getLogger(org.example.service_v2.APICaller.class);

    public APICaller() {}

    public APICaller(OkHttpClient okHttpClient) {

        this.okHttpClient = okHttpClient;
    }

    public List<JobLink> getJobs(Integer amount) {
        List<JobLink> jobUrls = new ArrayList<>();
        Request request = new Request.Builder()
                .url(SEARCH_JOB_URL + amount.toString())
                .get()
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if(response.code() == 200 && response.body() != null) {
                Gson gson = new Gson();
                String body = response.body().string();
                WrapperJobLink responseWrapper = gson.fromJson(body, WrapperJobLink.class);
                jobUrls = responseWrapper.getJobs();
            }
            logger.trace("Api call for 200 links returned code: {}", response.code());
            logger.info("Links: {}", jobUrls);
        } catch (IOException e) {
            logger.error("Request failed for API index = 200 : {}", e.getMessage());
        }
        return jobUrls;
    }

    public void updateAPICall(UpdateDTO url) {
        if(url.getUrl() == null || url.getUrl().isEmpty())
            return;
        Gson gson = new Gson();
        String stringBody = gson.toJson(url, UpdateDTO.class);
        RequestBody body = RequestBody.create(stringBody, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(UPDATE_JOBS_URL)
                .put(body)
                .build();
        try(Response response = okHttpClient.newCall(request).execute()){
            System.out.println("Update was successful for " + url.getUrl() + " Code: " + response.code() + "\n");
            logger.trace("Update was successful for " + url.getUrl() + " Code: " + response.code() + "\n");
        } catch (IOException e) {
            System.out.println("Could not update: " + e.getMessage());
            logger.error("Could not update: " + e.getMessage() , e);
        }
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
