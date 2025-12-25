package com.mira.returnremind.controller;

import com.mira.returnremind.model.*;
import com.mira.returnremind.repo.NotificationRepository;
import com.mira.returnremind.repo.PurchaseRepository;
import com.mira.returnremind.repo.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/users/{userId}/purchases")
public class PurchaseController {

    private final UserRepository userRepository;
    private final PurchaseRepository purchaseRepository;
    private final NotificationRepository notificationRepository;

    public PurchaseController(
            UserRepository userRepository,
            PurchaseRepository purchaseRepository,
            NotificationRepository notificationRepository
    ) {
        this.userRepository = userRepository;
        this.purchaseRepository = purchaseRepository;
        this.notificationRepository = notificationRepository;
    }

    record CreatePurchaseRequest(
            String merchantName,
            String itemName,
            LocalDate purchaseDate,
            Integer returnWindowDays
    ) {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Purchase createPurchase(@PathVariable Long userId, @RequestBody CreatePurchaseRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // Create purchase
        Purchase p = new Purchase();
        p.setUser(user);
        p.setMerchantName(req.merchantName());
        p.setItemName(req.itemName());
        p.setPurchaseDate(req.purchaseDate());
        p.setReturnWindowDays(req.returnWindowDays());

        // Calculate deadline
        LocalDate deadline = req.purchaseDate().plusDays(req.returnWindowDays());
        p.setReturnDeadline(deadline);

        Purchase saved = purchaseRepository.save(p);

        // Create notifications
        createNotification(saved, NotificationType.SEVEN_DAYS_BEFORE, deadline.minusDays(7));
        createNotification(saved, NotificationType.ONE_DAY_BEFORE, deadline.minusDays(1));

        return saved;
    }

    private void createNotification(Purchase purchase, NotificationType type, LocalDate date) {
        Notification n = new Notification();
        n.setPurchase(purchase);
        n.setType(type);

        // schedule at 9am local time (simple default)
        n.setScheduledFor(date.atTime(9, 0));
        n.setStatus(NotificationStatus.PENDING);

        notificationRepository.save(n);
    }


    // Get active (non-archived) purchases for a user
    @GetMapping
    public java.util.List<Purchase> getActivePurchases(@PathVariable Long userId) {
        return purchaseRepository.findByUserIdAndArchivedFalse(userId);
    }

    // Get archived purchases (history) for a user
    @GetMapping("/history")
    public java.util.List<Purchase> getArchivedPurchases(@PathVariable Long userId) {
        return purchaseRepository.findByUserIdAndArchivedTrue(userId);
    }
}