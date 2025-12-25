package com.mira.returnremind.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    private String merchantName;
    private String itemName;
    private LocalDate purchaseDate;
    private Integer returnWindowDays;
    private LocalDate returnDeadline;

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

    public Integer getReturnWindowDays() { return returnWindowDays; }
    public void setReturnWindowDays(Integer returnWindowDays) { this.returnWindowDays = returnWindowDays; }

    public LocalDate getReturnDeadline() { return returnDeadline; }
    public void setReturnDeadline(LocalDate returnDeadline) { this.returnDeadline = returnDeadline; }

}
