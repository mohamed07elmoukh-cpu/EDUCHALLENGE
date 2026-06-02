package com.educhallenge.gamification.repository;

import com.educhallenge.gamification.entity.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	List<Notification> findTop6ByUser_IdOrderByCreatedAtDesc(Long userId);
}
