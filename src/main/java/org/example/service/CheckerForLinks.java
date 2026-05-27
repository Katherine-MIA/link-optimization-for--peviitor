package org.example.service;


import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.TlsVersion;
//import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;


public class CheckerForLinks {
    private final OkHttpClient client;
    //private final Logger logger = LoggerFactory.getLogger(CheckerForLinks.class);
    private Logger logger = Logger.getLogger(CheckerForLinks.class.getName());
    private List<String> urls;

    public CheckerForLinks(OkHttpClient okHttpClient, List<String> links) {
        super();
        this.client = okHttpClient;
        this.urls = Collections.synchronizedList(links);
    }

    public Request createBrowserRequest(String url) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (httpUrl != null) {
            return new Request.Builder()
                    .url(httpUrl)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
                    .header("Accept-Language", "en-US,en;q=0.9")
                    // Client Hints: Modern browsers use these to identify their environment
                    .header("Sec-Ch-Ua", "\"Chromium\";v=\"124\", \"Google Chrome\";v=\"124\", \"Not-A.Brand\";v=\"99\"")
                    .header("Sec-Ch-Ua-Mobile", "?0")
                    .header("Sec-Ch-Ua-Platform", "\"Windows\"")
                    // Fetch Metadata: Tells the server this is a standard navigation request
                    .header("Sec-Fetch-Dest", "document")
                    .header("Sec-Fetch-Mode", "navigate")
                    .header("Sec-Fetch-Site", "none")
                    .header("Sec-Fetch-User", "?1")
                    .header("Upgrade-Insecure-Requests", "1")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Range", "bytes=0-4096") // Only grab the start of the page
                    .build();
        }
        return null;
    }

    private List<ConnectionSpec> connectionSpecs(){
        // 1. The Modern "Preferred" Spec (TLS 1.3 + Top Ciphers)
        ConnectionSpec modernSpec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_3, TlsVersion.TLS_1_2)
                .cipherSuites(
                        CipherSuite.TLS_AES_128_GCM_SHA256,
                        CipherSuite.TLS_AES_256_GCM_SHA384,
                        CipherSuite.TLS_CHACHA20_POLY1305_SHA256,
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256
                )
                .build();

        // 2. The Fallback Spec (Includes slightly older but still secure ciphers)
        ConnectionSpec compatibleSpec = new ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
                .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1)
                .build();

        return Arrays.asList(modernSpec,compatibleSpec, ConnectionSpec.CLEARTEXT);
    }

    private String cleanLink(String link){
        return HttpUrl.parse(link).toString().toLowerCase()
                .replace("https://", "")
                .replace("http://", "")
                .replace("www.", "")
                .replaceAll("/$", "");
    }

    private boolean compareLinks(String originalLink, String finalLink){
        String s1 = HttpUrl.parse(originalLink).toString();
        String s2 = HttpUrl.parse(finalLink).toString();
        //if(cleanLink(originalLink).equals(cleanLink(finalLink)))
        if(s1.equals(s2))
            return true;
        return false;
    }

    public boolean requestAsync(){
        
        return true;
    }

    public boolean requestSender(String url){
        Request request = createBrowserRequest(url);
        if(request == null) return false;
        String originalUrl = request.url().toString();
        OkHttpClient requestClient = client.newBuilder()
                .connectionSpecs(connectionSpecs())
                .cookieJar(new TransientCookieJar())
                .build();

        // The 'try-with-resources' ensures the response/connection is handled correctly
        try (Response response = requestClient.newCall(request).execute()) {
            // finalUrl is the URL AFTER all redirects have completed
            HttpUrl finalUrl = response.request().url();
            int code = response.code();

            // Determine link health
            // code 206 is returned because we requested a 'Range' (Partial Content)
            boolean isSuccess = (code == 200 || code == 206);
            boolean isSamePath = compareLinks(originalUrl, finalUrl.toString());

            if (isSuccess && isSamePath) {
                //logger.info(originalUrl, "ACTIVE", code);
                logger.info(originalUrl + " Active " + " Code: " + code);
                return true;
            } else if (isSuccess && !isSamePath) {
                // Check if it's just a minor path change or a totally different page (e.g., home page)
                //logger.warn(originalUrl, "REDIRECTED/EXPIRED (Final: " + finalUrl + ")", code);
                logger.warning(originalUrl + " Redirected " + finalUrl + "Code: " + code);
                return true;
            } else {
                //logger.info(originalUrl, "INACTIVE/ERROR", code);
                logger.info(originalUrl + " Inactive/Error " + code);
                return false;
            }

        } catch (IOException e) {
            // Host is likely down, DNS failed, or TLS handshake was rejected
            //logger.error(originalUrl, "HOST_UNREACHABLE: " + e.getMessage(), 0);
            logger.severe(originalUrl +  " IOException on connection" + e.getMessage());
        }
        return false;
    }

    public List<String> getUrls(){
        return urls;
    }

    public void startCheck(){
        for (int i = 0; i < urls.size(); i++) {
            if(requestSender(urls.get(i))) {
                urls.remove(i);
                i--;
            }
        }
    }

}
