package com.chittimanager.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

public class CreateChittiRequest {
    @NotBlank(message = "Chitty name is required")
    private String name;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;
    
    @NotNull(message = "Total members is required")
    @Positive(message = "Total members must be positive")
    private Integer totalMembers;
    
    @NotNull(message = "Total months is required")
    @Positive(message = "Total months must be positive")
    private Integer totalMonths;
    
    private List<String> memberNames;

    public CreateChittiRequest() {}

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public Integer getTotalMembers() { return totalMembers; }
    public void setTotalMembers(Integer totalMembers) { this.totalMembers = totalMembers; }

    public Integer getTotalMonths() { return totalMonths; }
    public void setTotalMonths(Integer totalMonths) { this.totalMonths = totalMonths; }

    public List<String> getMemberNames() { return memberNames; }
    public void setMemberNames(List<String> memberNames) { this.memberNames = memberNames; }
}