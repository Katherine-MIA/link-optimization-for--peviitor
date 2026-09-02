package org.example.service_v2;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.example.model.JobLink;

import java.io.IOException;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class LinkValidator {
    private final OkHttpClient client;
    private Logger logger = Logger.getLogger(LinkValidator.class.getName());
    private Queue<JobLink> urls;

    public LinkValidator(OkHttpClient okHttpClient, Queue<JobLink> urls) {
        super();
        this.client = okHttpClient;
        this.urls = urls;
    }

    public Request createRequest(String url) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (httpUrl == null)
            return null;
        else return new Request.Builder()
                    .url(httpUrl)
                    .build();
    }

    public boolean requestSendAsync(String url) throws InterruptedException {
        Request request = createRequest(url);
        if(Objects.isNull(request))
            return false;

        CompletableFuture<String> str = new CompletableFuture<>();
        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override public void onResponse(Call call, Response response) throws IOException {
                try(ResponseBody responseBody = response.body()) {
                    if(!response.isSuccessful() && response.code() != 404) {
                        throw new IOException("Failed with code " + response);
                    }
                    str.complete(String.valueOf(response.code()));
                }
            }
        });
        if(Objects.isNull(str) || str.equals("404")) return false;
        else return true;
    }

    public void startCheck() throws InterruptedException {
        for (JobLink url : urls) {
            Thread.sleep(5000);
            try {
                if (!requestSendAsync(url.getUrl())) {
                    urls.remove();
                }
            } catch(InterruptedException e) {
                logger.severe("Thread " + Thread.currentThread().getName() + " Error: " + e.getMessage());
            }
        }
    }

}
