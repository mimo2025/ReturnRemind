package com.mira.returnremind.service;

import com.mira.returnremind.model.Notification;
import com.mira.returnremind.model.NotificationStatus;
import com.mira.returnremind.model.NotificationType;
import com.mira.returnremind.model.Purchase;
import com.mira.returnremind.repo.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    public NotificationService(NotificationRepository notificationRepository, EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
    }

    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void sendDueNotifications() {
        LocalDateTime now = LocalDateTime.now();

        List<Notification> due =
                notificationRepository.findByStatusAndScheduledForBefore(NotificationStatus.PENDING, now.plusSeconds(1));

        for (Notification n : due) {
            Purchase p = n.getPurchase();

            // Skip if purchase missing or archived
            if (p == null || p.isArchived()) {
                n.setStatus(NotificationStatus.SKIPPED);
                continue;
            }

            LocalDate deadlineDate = p.getReturnDeadline();
            LocalDateTime deadlineStart = deadlineDate.atStartOfDay();
            LocalDateTime deadlineEnd = deadlineStart.plusDays(1);

            // If the deadline has already passed, skip
            if (now.isAfter(deadlineEnd)) {
                n.setStatus(NotificationStatus.SKIPPED);
                continue;
            }

            // DEADLINE_REACHED only fires on the deadline day
            if (n.getType() == NotificationType.DEADLINE_REACHED
                    && (now.isBefore(deadlineStart) || now.isAfter(deadlineEnd))) {
                n.setStatus(NotificationStatus.SKIPPED);
                continue;
            }

            // Send via EmailService
            try {
                emailService.sendNotificationEmail(n);
                n.setStatus(NotificationStatus.SENT);
                n.setSentAt(now);
            } catch (Exception e) {
                log.error("Failed to send notification {}: {}", n.getId(), e.getMessage());
                // Don't mark as SENT - will retry next cycle
            }
        }
    }
}