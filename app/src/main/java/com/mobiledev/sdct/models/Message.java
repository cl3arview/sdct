package com.mobiledev.sdct.models;

public class Message {
    private String senderId;
    private String text;
    private long timestamp;
    private String senderName;

    public Message() {
        // No-argument constructor required for Firestore
    }

    public Message(String senderId, String text, long timestamp, String senderName) {
        this.senderId = senderId;
        this.text = text;
        this.timestamp = timestamp;
        this.senderName = senderName;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}
