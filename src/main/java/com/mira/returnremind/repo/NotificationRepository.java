package com.mira.returnremind.repo;

import com.mira.returnremind.model.Notification;
import com.mira.returnremind.model.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByStatusAndScheduledForBefore(
            NotificationStatus status,
            LocalDateTime before
    );

    List<Notification> findByPurchaseIdAndStatus(Long purchaseId, NotificationStatus status);

    // NEW: Find upcoming pending notifications for a user
    @Query("SELECT n FROM Notification n " +
            "WHERE n.purchase.user.id = :userId " +
            "AND n.status = 'PENDING' " +
            "AND n.scheduledFor > :now " +
            "ORDER BY n.scheduledFor ASC")
    List<Notification> findUpcomingByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    // NEW: Find all notifications for a user
    @Query("SELECT n FROM Notification n " +
            "WHERE n.purchase.user.id = :userId " +
            "ORDER BY n.scheduledFor DESC")
    List<Notification> findAllByUserId(@Param("userId") Long userId);
}
