package com.chittimanager.controller;

import com.chittimanager.dto.ApiResponse;
import com.chittimanager.dto.CreateChittiRequest;
import com.chittimanager.model.Chitty;
import com.chittimanager.model.Member;
import com.chittimanager.service.ChittiService;
import com.chittimanager.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/chitties")
@Api(value = "Chitty Management", description = "Operations related to chitty management")
public class ChittiController {

    @Autowired
    private ChittiService chittiService;

    @Autowired
    private MemberService memberService;

    @ApiOperation(value = "Get all active chitties")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Chitty>>> getAllChitties() {
        List<Chitty> chitties = chittiService.getAllActiveChitties();
        return ResponseEntity.ok(ApiResponse.success(chitties));
    }

    @ApiOperation(value = "Get chitty by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Chitty>> getChittiById(@PathVariable String id) {
        Optional<Chitty> chitty = chittiService.getChittiById(id);
        if (chitty.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(chitty.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @ApiOperation(value = "Create a new chitty")
    @PostMapping
    public ResponseEntity<ApiResponse<Chitty>> createChitty(@Valid @RequestBody CreateChittiRequest request) {
        try {
            Chitty chitty = new Chitty(request.getName(), request.getAmount(), 
                                     request.getTotalMembers(), request.getTotalMonths());
            Chitty savedChitty = chittiService.createChitty(chitty);

            // Add members if provided
            if (request.getMemberNames() != null && !request.getMemberNames().isEmpty()) {
                for (String memberName : request.getMemberNames()) {
                    chittiService.addMemberToChitty(savedChitty.getId(), memberName);
                }
            }

            return ResponseEntity.ok(ApiResponse.success("Chitty created successfully", savedChitty));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to create chitty: " + e.getMessage()));
        }
    }

    @ApiOperation(value = "Update chitty")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Chitty>> updateChitty(@PathVariable String id, @Valid @RequestBody CreateChittiRequest request) {
        try {
            Chitty chittyDetails = new Chitty(request.getName(), request.getAmount(), 
                                            request.getTotalMembers(), request.getTotalMonths());
            Chitty updatedChitty = chittiService.updateChitty(id, chittyDetails);
            if (updatedChitty != null) {
                return ResponseEntity.ok(ApiResponse.success("Chitty updated successfully", updatedChitty));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update chitty: " + e.getMessage()));
        }
    }

    @ApiOperation(value = "Delete chitty")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteChitty(@PathVariable String id) {
        if (chittiService.deleteChitty(id)) {
            return ResponseEntity.ok(ApiResponse.success("Chitty deleted successfully", "Deleted"));
        }
        return ResponseEntity.notFound().build();
    }

    @ApiOperation(value = "Add member to chitty")
    @PostMapping("/{id}/members")
    public ResponseEntity<ApiResponse<Chitty>> addMember(@PathVariable String id, @RequestParam String memberName) {
        try {
            Chitty updatedChitty = chittiService.addMemberToChitty(id, memberName);
            if (updatedChitty != null) {
                return ResponseEntity.ok(ApiResponse.success("Member added successfully", updatedChitty));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to add member: " + e.getMessage()));
        }
    }

    @ApiOperation(value = "Remove member from chitty")
    @DeleteMapping("/{id}/members/{memberId}")
    public ResponseEntity<ApiResponse<String>> removeMember(@PathVariable String id, @PathVariable String memberId) {
        if (chittiService.removeMemberFromChitty(id, memberId)) {
            return ResponseEntity.ok(ApiResponse.success("Member removed successfully", "Removed"));
        }
        return ResponseEntity.notFound().build();
    }

    @ApiOperation(value = "Get members of a chitty")
    @GetMapping("/{id}/members")
    public ResponseEntity<ApiResponse<List<Member>>> getChittiMembers(@PathVariable String id) {
        List<Member> members = memberService.getMembersByChittiId(id);
        return ResponseEntity.ok(ApiResponse.success(members));
    }

    @ApiOperation(value = "Search chitties by name")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Chitty>>> searchChitties(@RequestParam String query) {
        List<Chitty> chitties = chittiService.searchChitties(query);
        return ResponseEntity.ok(ApiResponse.success(chitties));
    }
}