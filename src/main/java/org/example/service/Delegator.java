package org.example.service;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;

public class Delegator {
    private CloseableHttpClient httpClient;

    public Delegator() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultMaxPerRoute(10);
        connectionManager.setMaxTotal(20);
        this.httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();
    }

    //public void
}
