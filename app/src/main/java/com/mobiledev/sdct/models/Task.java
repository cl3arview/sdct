package com.mobiledev.sdct.models;

import java.util.Objects;

public class Task {
    private String id;
    private String text;
    private long timestamp;
    private boolean completed;
    private String userId;

    public Task() {
        // No-argument constructor required for Firestore
    }

    public Task(String text, long timestamp, boolean completed, String userId) {
        this.text = text;
        this.timestamp = timestamp;
        this.completed = completed;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
