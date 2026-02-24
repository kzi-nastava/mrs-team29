package service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import domain.entities.Notification;
import domain.entities.Ride;
import domain.entities.User;
import domain.enums.NotificationType;
import dto.notification.NotificationResponseDTO;
import repository.NotificationRepository;
import service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public NotificationResponseDTO createNotification(User user, Ride ride, NotificationType type, String title, String message) {
        Notification notification = new Notification(user, ride, type, title, message);
        notification = notificationRepository.save(notification);
        return NotificationResponseDTO.fromNotification(notification);
    }

    @Override
    public List<NotificationResponseDTO> getUserNotifications(String userId) {
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationResponseDTO::fromNotification)
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
