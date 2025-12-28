package com.mira.returnremind.controller;

import com.mira.returnremind.model.Notification;
import com.mira.returnremind.repo.NotificationRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @GetMapping
    public List<Notification> getUpcomingNotifications(@PathVariable Long userId) {
        return notificationRepository.findUpcomingByUserId(userId, LocalDateTime.now());
    }

    @GetMapping("/all")
    public List<Notification> getAllNotifications(@PathVariable Long userId) {
        return notificationRepository.findAllByUserId(userId);
    }
}