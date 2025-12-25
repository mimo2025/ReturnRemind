package com.mira.returnremind.repo;

import com.mira.returnremind.model.Notification;
import com.mira.returnremind.model.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByStatusAndScheduledForBefore(
            NotificationStatus status,
            LocalDateTime before
    );
}
