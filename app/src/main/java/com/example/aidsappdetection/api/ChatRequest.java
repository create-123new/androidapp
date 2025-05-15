package com.example.aidsappdetection.api;

public class ChatRequest {
    private String query;

    public ChatRequest(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
