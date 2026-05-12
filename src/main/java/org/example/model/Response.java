package org.example.model;

import java.util.List;

public class Response {
    private List<Job> docs;
    private Integer numFound;

    public Response() {
    }

    public Response(List<Job> docs, Integer numFound) {
        this.docs = docs;
        this.numFound = numFound;
    }

    public List<Job> getDocs() {
        return docs;
    }

    public void setDocs(List<Job> docs) {
        this.docs = docs;
    }

    public Integer getNumFound() {
        return numFound;
    }

    public void setNumFound(Integer numFound) {
        this.numFound = numFound;
    }

    @Override
    public String toString() {
        return "Response{" +
                "docs=" + docs +
                ", numFound=" + numFound +
                '}';
    }
}
