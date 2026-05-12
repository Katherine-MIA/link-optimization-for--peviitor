package org.example.model;

import java.util.List;

public class UrlsDTO {
    private List<String> urls;

    public UrlsDTO() {
    }

    public UrlsDTO(List<String> urls) {
        this.urls = urls;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
}
