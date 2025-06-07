package com.chittimanager.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "members")
public class Member {
    @Id
    private String id;
    private String name;
    private String chittiId;
    private boolean hasLifted;
    private LocalDateTime liftedDate;
    private int liftedMonth;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Member() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.hasLifted = false;
        this.liftedMonth = -1;
    }

    public Member(String name, String chittiId) {
        this();
        this.name = name;
        this.chittiId = chittiId;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getChittiId() { return chittiId; }
    public void setChittiId(String chittiId) { this.chittiId = chittiId; }

    public boolean isHasLifted() { return hasLifted; }
    public void setHasLifted(boolean hasLifted) { 
        this.hasLifted = hasLifted;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getLiftedDate() { return liftedDate; }
    public void setLiftedDate(LocalDateTime liftedDate) { 
        this.liftedDate = liftedDate;
        this.updatedAt = LocalDateTime.now();
    }

    public int getLiftedMonth() { return liftedMonth; }
    public void setLiftedMonth(int liftedMonth) { 
        this.liftedMonth = liftedMonth;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}