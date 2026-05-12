package org.example.model;

public class ResponseWrapper {
    Response response;

    public ResponseWrapper() {
    }

    public ResponseWrapper(Response response) {
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "ResponseWrapper{" +
                "response=" + response +
                '}';
    }
}
