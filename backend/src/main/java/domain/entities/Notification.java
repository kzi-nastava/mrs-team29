package domain.entities;

import java.time.LocalDateTime;

import domain.enums.NotificationType;
import jakarta.persistence.*;

@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "ride_id")
    private Ride ride;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_read", nullable = false)
    private boolean read;

    public Notification() {}

    public Notification(User user, Ride ride, NotificationType type, String title, String message) {
        this.user = user;
        this.ride = ride;
        this.type = type;
        this.title = title;
        this.message = message;
        this.createdAt = LocalDateTime.now();
        this.read = false;
    }

    public String getId() { return id; }
    public User getUser() { return user; }
    public Ride getRide() { return ride; }
    public NotificationType getType() { return type; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isRead() { return read; }

    public void setId(String id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setRide(Ride ride) { this.ride = ride; }
    public void setType(NotificationType type) { this.type = type; }
    public void setTitle(String title) { this.title = title; }
    public void setMessage(String message) { this.message = message; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setRead(boolean read) { this.read = read; }
}
