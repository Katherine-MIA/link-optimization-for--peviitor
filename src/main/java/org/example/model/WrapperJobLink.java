package org.example.model;

import java.util.List;

public class WrapperJobLink {
    Integer remaining;
    Integer index;
    Integer found;
    List<JobLink> jobs;

    public WrapperJobLink() {
    }

    public WrapperJobLink(List<JobLink> jobs, Integer found, Integer index, Integer remaining) {
        this.jobs = jobs;
        this.found = found;
        this.index = index;
        this.remaining = remaining;
    }

    public Integer getRemaining() {
        return remaining;
    }

    public void setRemaining(Integer remaining) {
        this.remaining = remaining;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getFound() {
        return found;
    }

    public void setFound(Integer found) {
        this.found = found;
    }

    public List<JobLink> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobLink> jobs) {
        this.jobs = jobs;
    }
}
