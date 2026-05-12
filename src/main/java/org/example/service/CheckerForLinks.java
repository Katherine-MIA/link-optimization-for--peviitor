package org.example.service;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

import java.util.List;

public class CheckerForLinks extends Thread {
    CloseableHttpClient httpClient;
    List<String> urls;

    public CheckerForLinks(CloseableHttpClient client, List<String> urls) {
        httpClient = client;

    }


}
