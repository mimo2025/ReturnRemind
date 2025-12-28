package com.mira.returnremind.controller;

import com.mira.returnremind.model.Purchase;
import com.mira.returnremind.service.PurchaseService;
import com.mira.returnremind.repo.PurchaseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final PurchaseRepository purchaseRepository;

    public PurchaseController(PurchaseService purchaseService, PurchaseRepository purchaseRepository) {
        this.purchaseService = purchaseService;
        this.purchaseRepository = purchaseRepository;
    }

    public record CreatePurchaseRequest(
            String merchantName,
            String itemName,
            LocalDate purchaseDate,
            Integer returnWindowDays
    ) {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Purchase createPurchase(@PathVariable Long userId, @RequestBody CreatePurchaseRequest req) {
        return purchaseService.createPurchase(
                userId,
                req.merchantName(),
                req.itemName(),
                req.purchaseDate(),
                req.returnWindowDays()
        );
    }

    @GetMapping
    public List<Purchase> getActivePurchases(@PathVariable Long userId) {
        return purchaseRepository.findByUserIdAndArchivedFalse(userId);
    }

    @GetMapping("/history")
    public List<Purchase> getArchivedPurchases(@PathVariable Long userId) {
        return purchaseRepository.findByUserIdAndArchivedTrue(userId);
    }
}