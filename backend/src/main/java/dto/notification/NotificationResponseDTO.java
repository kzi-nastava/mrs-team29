package dto.notification;

import java.time.LocalDateTime;

import domain.entities.Notification;

public class NotificationResponseDTO {
    private String id;
    private String title;
    private String message;
    private String type;
    private String rideId;
    private LocalDateTime createdAt;
    private boolean read;

    public static NotificationResponseDTO fromNotification(Notification notification) {
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.id = notification.getId();
        dto.title = notification.getTitle();
        dto.message = notification.getMessage();
        dto.type = notification.getType() == null ? null : notification.getType().name();
        dto.rideId = notification.getRide() == null ? null : notification.getRide().getId();
        dto.createdAt = notification.getCreatedAt();
        dto.read = notification.isRead();
        return dto;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public String getRideId() { return rideId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isRead() { return read; }
}
