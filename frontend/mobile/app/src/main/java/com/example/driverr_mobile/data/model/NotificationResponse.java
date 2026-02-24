package com.example.driverr_mobile.data.model;

public class NotificationResponse {
    private String id;
    private String title;
    private String message;
    private String type;
    private String rideId;
    private String createdAt;
    private boolean read;

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public String getRideId() { return rideId; }
    public String getCreatedAt() { return createdAt; }
    public boolean isRead() { return read; }
}
