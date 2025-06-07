package com.chittimanager.controller;

import com.chittimanager.dto.ApiResponse;
import com.chittimanager.model.MonthlySlip;
import com.chittimanager.service.MonthlySlipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/monthly-slips")
@Tag(name = "Monthly Slip Management", description = "Operations related to monthly slip management")
public class MonthlySlipController {

    @Autowired
    private MonthlySlipService monthlySlipService;

    @Operation(summary = "Get all slips for a chitty")
    @GetMapping("/chitty/{chittiId}")
    public ResponseEntity<ApiResponse<List<MonthlySlip>>> getSlipsByChittiId(@PathVariable String chittiId) {
        List<MonthlySlip> slips = monthlySlipService.getSlipsByChittiId(chittiId);
        return ResponseEntity.ok(ApiResponse.success(slips));
    }

    @Operation(summary = "Get slip for specific chitty and month")
    @GetMapping("/chitty/{chittiId}/month/{month}")
    public ResponseEntity<ApiResponse<MonthlySlip>> getSlipByChittiAndMonth(@PathVariable String chittiId, @PathVariable int month) {
        Optional<MonthlySlip> slip = monthlySlipService.getSlipByChittiIdAndMonth(chittiId, month);
        if (slip.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(slip.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Generate monthly slip for a chitty")
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<MonthlySlip>> generateMonthlySlip(@RequestParam String chittiId, @RequestParam int month) {
        try {
            MonthlySlip slip = monthlySlipService.generateMonthlySlip(chittiId, month);
            if (slip != null) {
                return ResponseEntity.ok(ApiResponse.success("Monthly slip generated successfully", slip));
            }
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to generate monthly slip"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to generate monthly slip: " + e.getMessage()));
        }
    }

    @Operation(summary = "Mark member as lifted in slip")
    @PostMapping("/{slipId}/lift")
    public ResponseEntity<ApiResponse<MonthlySlip>> markMemberAsLifted(@PathVariable String slipId, @RequestParam String memberId) {
        try {
            MonthlySlip slip = monthlySlipService.markMemberAsLifted(slipId, memberId);
            if (slip != null) {
                return ResponseEntity.ok(ApiResponse.success("Member marked as lifted in slip", slip));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to mark member as lifted: " + e.getMessage()));
        }
    }

    @Operation(summary = "Mark payment status")
    @PostMapping("/{slipId}/payment")
    public ResponseEntity<ApiResponse<MonthlySlip>> markPayment(@PathVariable String slipId, @RequestParam String memberId, @RequestParam boolean isPaid) {
        try {
            MonthlySlip slip = monthlySlipService.markPayment(slipId, memberId, isPaid);
            if (slip != null) {
                return ResponseEntity.ok(ApiResponse.success("Payment status updated", slip));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update payment status: " + e.getMessage()));
        }
    }

    @Operation(summary = "Delete monthly slip")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteSlip(@PathVariable String id) {
        if (monthlySlipService.deleteSlip(id)) {
            return ResponseEntity.ok(ApiResponse.success("Monthly slip deleted successfully", "Deleted"));
        }
        return ResponseEntity.notFound().build();
    }
}