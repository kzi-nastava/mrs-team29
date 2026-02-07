package dto.user;

import domain.enums.ChangeRequestStatus;
import java.time.LocalDateTime;

public class ProfileChangeRequestDTO {
    
    private String id;
    private String userId;
    private String userName;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private ChangeRequestStatus status;
    private LocalDateTime createdAt;
    
    public ProfileChangeRequestDTO() {}
    
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getFieldName() { return fieldName; }
    public String getOldValue() { return oldValue; }
    public String getNewValue() { return newValue; }
    public ChangeRequestStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }
    public void setStatus(ChangeRequestStatus status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
