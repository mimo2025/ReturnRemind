package com.mira.returnremind.service;

import com.mira.returnremind.model.*;
import com.mira.returnremind.repo.NotificationRepository;
import com.mira.returnremind.repo.PurchaseRepository;
import com.mira.returnremind.repo.UserRepository;
import com.mira.returnremind.model.NotificationType;
import com.mira.returnremind.model.Purchase;
import com.mira.returnremind.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import java.util.List;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepo;
    private final UserRepository userRepo;
    private final NotificationRepository notificationRepo;

    public PurchaseService(PurchaseRepository purchaseRepo,
                           UserRepository userRepo,
                           NotificationRepository notificationRepo) {
        this.purchaseRepo = purchaseRepo;
        this.userRepo = userRepo;
        this.notificationRepo = notificationRepo;
    }

    @Transactional
    public Purchase createPurchase(Long userId,
                                   String merchantName,
                                   String itemName,
                                   LocalDate purchaseDate,
                                   int returnWindowDays) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Purchase p = new Purchase();
        p.setUser(user);
        p.setMerchantName(merchantName);
        p.setItemName(itemName);
        p.setPurchaseDate(purchaseDate);
        p.setReturnWindowDays(returnWindowDays);

        LocalDate deadline = purchaseDate.plusDays(returnWindowDays);
        p.setReturnDeadline(deadline);

        Purchase saved = purchaseRepo.save(p);
        scheduleNotifications(saved);
        return saved;
    }

    private void scheduleNotifications(Purchase p) {
        LocalDate deadline = p.getReturnDeadline();

        LocalDateTime sevenDaysBefore = deadline.minusDays(7).atStartOfDay();
        LocalDateTime oneDayBefore    = deadline.minusDays(1).atStartOfDay();
        LocalDateTime deadlineReached = deadline.atStartOfDay();

        if (sevenDaysBefore.isAfter(LocalDateTime.now())) {
            createNotification(p, NotificationType.SEVEN_DAYS_BEFORE, sevenDaysBefore);
        }
        if (oneDayBefore.isAfter(LocalDateTime.now())) {
            createNotification(p, NotificationType.ONE_DAY_BEFORE, oneDayBefore);
        }
        if (!deadlineReached.isBefore(LocalDateTime.now())) {
            createNotification(p, NotificationType.DEADLINE_REACHED, deadlineReached);
        } else {
            // If deadlineReached is already in the past, still create it as due *now* (optional)
            createNotification(p, NotificationType.DEADLINE_REACHED, LocalDateTime.now());
        }
    }

    private void createNotification(Purchase p,
                                    NotificationType type,
                                    LocalDateTime scheduledFor) {

        Notification n = new Notification();
        n.setPurchase(p);
        n.setType(type);
        n.setScheduledFor(scheduledFor);
        n.setStatus(NotificationStatus.PENDING);

        notificationRepo.save(n);
    }

    @Scheduled(cron = "0 0 2 * * *") // daily at 2am
    @Transactional
    public void archiveExpiredPurchases() {
        LocalDate today = LocalDate.now();
        List<Purchase> expired = purchaseRepo.findByReturnDeadlineBeforeAndArchivedFalse(today);

        for (Purchase p : expired) {
            List<Notification> pending = notificationRepo.findByPurchaseIdAndStatus(
                    p.getId(), NotificationStatus.PENDING
            );

            for (Notification n : pending) {
                n.setStatus(NotificationStatus.SKIPPED);
            }
            p.setArchived(true);
            p.setArchivedAt(LocalDateTime.now());
        }
    }

}