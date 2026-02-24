package service;

import java.util.List;

import domain.entities.Ride;
import domain.entities.User;
import domain.enums.NotificationType;
import dto.notification.NotificationResponseDTO;

public interface NotificationService {
    NotificationResponseDTO createNotification(User user, Ride ride, NotificationType type, String title, String message);
    List<NotificationResponseDTO> getUserNotifications(String userId);
    void markAsRead(String notificationId);
}
