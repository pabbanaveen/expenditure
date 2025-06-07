package com.chittimanager.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "chitties")
public class Chitty {
    @Id
    private String id;
    private String name;
    private double amount;
    private int totalMembers;
    private int totalMonths;
    private double regularPayment;
    private double liftedPayment;
    private LocalDateTime startDate;
    private boolean isActive;
    private List<String> memberIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Chitty() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.memberIds = new ArrayList<>();
        this.isActive = true;
    }

    public Chitty(String name, double amount, int totalMembers, int totalMonths) {
        this();
        this.name = name;
        this.amount = amount;
        this.totalMembers = totalMembers;
        this.totalMonths = totalMonths;
        this.calculatePayments();
    }

    private void calculatePayments() {
        this.regularPayment = this.amount / this.totalMonths;
        this.liftedPayment = this.regularPayment + (this.regularPayment * 0.25); // 25% extra for lifted members
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { 
        this.amount = amount;
        this.calculatePayments();
        this.updatedAt = LocalDateTime.now();
    }

    public int getTotalMembers() { return totalMembers; }
    public void setTotalMembers(int totalMembers) { 
        this.totalMembers = totalMembers;
        this.updatedAt = LocalDateTime.now();
    }

    public int getTotalMonths() { return totalMonths; }
    public void setTotalMonths(int totalMonths) { 
        this.totalMonths = totalMonths;
        this.calculatePayments();
        this.updatedAt = LocalDateTime.now();
    }

    public double getRegularPayment() { return regularPayment; }
    public void setRegularPayment(double regularPayment) { this.regularPayment = regularPayment; }

    public double getLiftedPayment() { return liftedPayment; }
    public void setLiftedPayment(double liftedPayment) { this.liftedPayment = liftedPayment; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { 
        this.startDate = startDate;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { 
        this.isActive = active;
        this.updatedAt = LocalDateTime.now();
    }

    public List<String> getMemberIds() { return memberIds; }
    public void setMemberIds(List<String> memberIds) { 
        this.memberIds = memberIds;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}