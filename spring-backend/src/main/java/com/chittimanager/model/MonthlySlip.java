package com.chittimanager.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "monthly_slips")
public class MonthlySlip {
    @Id
    private String id;
    private String chittiId;
    private int month;
    private LocalDateTime slipDate;
    private String liftedMemberId;
    private List<PaymentRecord> paymentRecords;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MonthlySlip() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.paymentRecords = new ArrayList<>();
    }

    public MonthlySlip(String chittiId, int month) {
        this();
        this.chittiId = chittiId;
        this.month = month;
        this.slipDate = LocalDateTime.now();
    }

    // Inner class for payment records
    public static class PaymentRecord {
        private String memberId;
        private String memberName;
        private double amount;
        private boolean isPaid;
        private boolean isLifted;
        private LocalDateTime paymentDate;

        public PaymentRecord() {}

        public PaymentRecord(String memberId, String memberName, double amount, boolean isLifted) {
            this.memberId = memberId;
            this.memberName = memberName;
            this.amount = amount;
            this.isLifted = isLifted;
            this.isPaid = false;
        }

        // Getters and Setters
        public String getMemberId() { return memberId; }
        public void setMemberId(String memberId) { this.memberId = memberId; }

        public String getMemberName() { return memberName; }
        public void setMemberName(String memberName) { this.memberName = memberName; }

        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }

        public boolean isPaid() { return isPaid; }
        public void setPaid(boolean paid) { 
            this.isPaid = paid;
            if (paid && this.paymentDate == null) {
                this.paymentDate = LocalDateTime.now();
            }
        }

        public boolean isLifted() { return isLifted; }
        public void setLifted(boolean lifted) { this.isLifted = lifted; }

        public LocalDateTime getPaymentDate() { return paymentDate; }
        public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getChittiId() { return chittiId; }
    public void setChittiId(String chittiId) { this.chittiId = chittiId; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public LocalDateTime getSlipDate() { return slipDate; }
    public void setSlipDate(LocalDateTime slipDate) { this.slipDate = slipDate; }

    public String getLiftedMemberId() { return liftedMemberId; }
    public void setLiftedMemberId(String liftedMemberId) { 
        this.liftedMemberId = liftedMemberId;
        this.updatedAt = LocalDateTime.now();
    }

    public List<PaymentRecord> getPaymentRecords() { return paymentRecords; }
    public void setPaymentRecords(List<PaymentRecord> paymentRecords) { this.paymentRecords = paymentRecords; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}