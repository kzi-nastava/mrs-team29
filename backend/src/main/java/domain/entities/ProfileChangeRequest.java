package domain.entities;

import java.time.LocalDateTime;

import domain.enums.*;

public class ProfileChangeRequest {
    private String id;
    private String userId;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private ChangeRequestStatus status;
    private LocalDateTime createdAt;
    
    public ProfileChangeRequest() {}
    
    public ProfileChangeRequest(String id, String userId, String fieldName, 
    		String oldValue, String newValue, ChangeRequestStatus status, LocalDateTime createdAt) {
    	this.id = id;
    	this.userId = userId;
    	this.fieldName = fieldName;
    	this.oldValue = oldValue;
    	this.newValue = newValue;
    	this.status = status;
    	this.createdAt = createdAt;
    }
    
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getFieldName() { return fieldName; }
    public String getOldValue() { return oldValue; }
    public String getNewValue() { return newValue; }
    public ChangeRequestStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }
    public void setStatus(ChangeRequestStatus status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
}

