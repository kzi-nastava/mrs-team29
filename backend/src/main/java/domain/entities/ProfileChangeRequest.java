package domain.entities;

import domain.enums.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Field name cannot be blank")
    @Column(name = "field_name", nullable = false)
    private String fieldName;

    @Column(columnDefinition = "TEXT", name = "old_value")
    private String oldValue;

    @Column(columnDefinition = "TEXT", name = "new_value")
    private String newValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChangeRequestStatus status;

    @Column(name = "created_at", nullable = false)
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
    public User getUser() { return user; }
    public String getFieldName() { return fieldName; }
    public String getOldValue() { return oldValue; }
    public String getNewValue() { return newValue; }
    public ChangeRequestStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    public void setId(String id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }
    public void setStatus(ChangeRequestStatus status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
}

