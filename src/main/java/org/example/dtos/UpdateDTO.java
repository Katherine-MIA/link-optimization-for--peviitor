package org.example.dtos;

public class UpdateDTO {
    String url;
    JobStatus status;

    public UpdateDTO() {
    }

    public UpdateDTO(String url, JobStatus status) {
        this.url = url;
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }
}
