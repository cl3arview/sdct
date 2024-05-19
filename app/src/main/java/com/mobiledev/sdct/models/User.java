package com.mobiledev.sdct.models;

public class User {
    private String uid;
    private String name;

    public User() {
        // No-argument constructor required for Firestore
    }

    public User(String name, String uid) {
        this.uid = uid;
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
