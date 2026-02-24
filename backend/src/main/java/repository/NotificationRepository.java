package repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import domain.entities.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findByUser_IdOrderByCreatedAtDesc(String userId);
}
