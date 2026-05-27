package org.example.service_invalid;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class RequestMaker implements Runnable {
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final List<String> checkURL;
    private final Logger logger = Logger.getLogger(ActionInitiator.class.getName());
    private final Lock lock = new ReentrantLock();

    public RequestMaker(List<String> checkUrls) {
        this.checkURL = Collections.synchronizedList(checkUrls);
        // -Djdk.httpclient.keepalive.timeout=2 -> JVM var for connection close
        // Could be improved
        //httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    }

    public List<String> getRemainingURLs() {
        return checkURL;
    }

    private String encodeURL(String url){
        return URLEncoder.encode(url, StandardCharsets.UTF_8);
    }

    @Override
    public void run() {
        if (checkURL == null || checkURL.isEmpty())
            return;
        for (int i = 0; i < checkURL.size(); i++) {
            try {
                HttpRequest getRequest = HttpRequest.newBuilder()
                        .uri(new URI(encodeURL(checkURL.get(i))))
                        .build();
                HttpResponse<String> getResponse;
                //getResponse = httpClient.send(headRequest, HttpResponse.BodyHandlers.ofString());
                //Thread lock logic
                lock.lock();
                try {
                    getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
                } finally {
                    lock.unlock();
                }
                if (getResponse.statusCode() == 429) {
                    i--;
                    Thread.sleep(4000);
                    continue;
                }
                if (getResponse.statusCode() >= 200 && getResponse.statusCode() < 300) {
                    checkURL.remove(i);
                    i--;
                }
            } catch (URISyntaxException | IOException e) {
                if (i + 1 != checkURL.size()) {
                    checkURL.removeAll(checkURL.subList(i + 1, checkURL.size()));
                }
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                checkURL.removeAll(checkURL.subList(i + 1, checkURL.size()));
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                logger.warning("WEIRED URL: " + checkURL.get(i) + " From: " + Thread.currentThread().getName());
                checkURL.remove(i);
            }
        }
        System.out.println(Thread.currentThread().getName() + "Has finished executing; No_links = " + checkURL.size());
    }
}
