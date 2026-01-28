package domain.entities;

import domain.enums.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "profile_change_requests")
public class ProfileChangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String fieldName;

    @Column(columnDefinition = "TEXT")
    private String oldValue;

    @Column(columnDefinition = "TEXT")
    private String newValue;

    @Enumerated(EnumType.STRING)
    private ChangeRequestStatus status;

    private LocalDateTime createdAt;

    public ProfileChangeRequest() {}

    public ProfileChangeRequest(User user, String fieldName,
                                String oldValue, String newValue) {
        this.user = user;
        this.fieldName = fieldName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.status = ChangeRequestStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
    
    public String getId() { return id; }
    public User getUserId() { return user; }
    public String getFieldName() { return fieldName; }
    public String getOldValue() { return oldValue; }
    public String getNewValue() { return newValue; }
    public ChangeRequestStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    public void setId(String id) { this.id = id; }
    public void setUserId(User user) { this.user = user; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }
    public void setStatus(ChangeRequestStatus status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
}

