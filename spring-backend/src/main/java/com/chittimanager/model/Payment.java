package com.chittimanager.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "payments")
public class Payment {
    @Id
    private String id;
    private String chittiId;
    private String memberId;
    private int month;
    private double amount;
    private boolean isPaid;
    private LocalDateTime paymentDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Payment() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isPaid = false;
    }

    public Payment(String chittiId, String memberId, int month, double amount) {
        this();
        this.chittiId = chittiId;
        this.memberId = memberId;
        this.month = month;
        this.amount = amount;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getChittiId() { return chittiId; }
    public void setChittiId(String chittiId) { this.chittiId = chittiId; }

    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { 
        this.isPaid = paid;
        if (paid && this.paymentDate == null) {
            this.paymentDate = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}