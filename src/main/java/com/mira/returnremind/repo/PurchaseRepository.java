package com.mira.returnremind.repo;

import com.mira.returnremind.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByUserIdAndArchivedFalse(Long userId);
    List<Purchase> findByUserIdAndArchivedTrue(Long userId);
    List<Purchase> findByReturnDeadlineBeforeAndArchivedFalse(LocalDate date);
}
