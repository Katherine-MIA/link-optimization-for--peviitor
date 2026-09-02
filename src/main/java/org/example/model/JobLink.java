package org.example.model;

public class JobLink {
    String url;

    public JobLink(String jobURL) {
        this.url = jobURL;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String jobURL) {
        this.url = jobURL;
    }

    @Override
    public String toString() {
        return "url: " + url + "\n";
    }
}
