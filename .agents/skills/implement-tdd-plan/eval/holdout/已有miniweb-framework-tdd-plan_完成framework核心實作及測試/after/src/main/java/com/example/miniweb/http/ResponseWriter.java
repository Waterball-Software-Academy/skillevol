package com.example.miniweb.http;

public class ResponseWriter {
    private int status = 200;
    private String body = "";

    public void writeText(int status, String body) {
        this.status = status;
        this.body = body;
    }

    public int getStatus() {
        return status;
    }

    public String getBody() {
        return body;
    }
}
