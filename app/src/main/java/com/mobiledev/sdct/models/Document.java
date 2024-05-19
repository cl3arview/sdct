package com.mobiledev.sdct.models;

public class Document {
    private String name;
    private String url;
    private String userId;

    public Document() {
        // No-argument constructor required for Firestore
    }

    public Document(String name, String url, String userId) {
        this.name = name;
        this.url = url;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
