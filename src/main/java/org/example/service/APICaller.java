package org.example.service;

import com.google.gson.Gson;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.example.model.ResponseWrapper;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

public class APICaller {
    private final String GET_URL = "https://api.peviitor.ro/v1/search/?page=";
    private final String DELETE_URL = "https://api.peviitor.ro/v1/delete/";
    private CloseableHttpClient httpClient;
    private Logger logger ;


    public APICaller(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public ResponseWrapper getRequestPerPage(String pageNumber) {
        HttpGet httpGet = new HttpGet(GET_URL + pageNumber);
        CloseableHttpResponse closeableResponse;
        ResponseWrapper responseWrapper = null;
        try {
            closeableResponse = httpClient.execute(httpGet);
            if(closeableResponse.getCode() == 200){
                Gson gson = new Gson();
                responseWrapper = gson.fromJson(EntityUtils.toString(closeableResponse.getEntity()), ResponseWrapper.class);
            }
//            String jsonString = httpClient.execute(httpGet, response -> {
//                return EntityUtils.toString(response.getEntity());
//            });
//            Gson gson = new Gson();
//            ResponseWrapper responseWrapper = gson.fromJson(jsonString, ResponseWrapper.class);
        }catch (IOException | ParseException e) {
            System.out.println(e.getMessage());
        }
//         finally {
//            //EntityUtils.consume(closeableResponse.getEntity());
//        }
        return responseWrapper;
    }

    public String deleteRequest(List<String> urls){
        Gson gson = new Gson();
        HttpDelete delete = new HttpDelete(DELETE_URL);
        delete.setEntity(new StringEntity(gson.toJson(urls)));

        HttpClientResponseHandler handler = response -> {
            try (HttpEntity entity = response.getEntity()) {
                return EntityUtils.toString(entity);
            }
        };
        CloseableHttpResponse response = null;
        try {
            response = (CloseableHttpResponse) httpClient.execute(delete, handler);
        } catch (IOException e) {
            logger.info("Error on delete: ", e);
            return "Error";
        }
        return response.getEntity().toString();
    }
}
