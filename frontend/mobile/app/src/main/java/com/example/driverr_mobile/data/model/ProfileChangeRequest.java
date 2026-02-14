package com.example.driverr_mobile.data.model;

public class ProfileChangeRequest {
    private String id;
    private String userId;
    private String userName;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private String status;
    private String createdAt;

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getFieldName() { return fieldName; }
    public String getOldValue() { return oldValue; }
    public String getNewValue() { return newValue; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
}
