package com.chittimanager.controller;

import com.chittimanager.dto.ApiResponse;
import com.chittimanager.model.Member;
import com.chittimanager.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/members")
@Api(value = "Member Management", description = "Operations related to member management")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @ApiOperation(value = "Get member by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Member>> getMemberById(@PathVariable String id) {
        Optional<Member> member = memberService.getMemberById(id);
        if (member.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(member.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @ApiOperation(value = "Update member")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Member>> updateMember(@PathVariable String id, @RequestBody Member memberDetails) {
        try {
            Member updatedMember = memberService.updateMember(id, memberDetails);
            if (updatedMember != null) {
                return ResponseEntity.ok(ApiResponse.success("Member updated successfully", updatedMember));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update member: " + e.getMessage()));
        }
    }

    @ApiOperation(value = "Mark member as lifted")
    @PostMapping("/{id}/lift")
    public ResponseEntity<ApiResponse<Member>> markMemberAsLifted(@PathVariable String id, @RequestParam int month) {
        try {
            Member member = memberService.markMemberAsLifted(id, month);
            if (member != null) {
                return ResponseEntity.ok(ApiResponse.success("Member marked as lifted", member));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to mark member as lifted: " + e.getMessage()));
        }
    }

    @ApiOperation(value = "Get lifted members of a chitty")
    @GetMapping("/chitty/{chittiId}/lifted")
    public ResponseEntity<ApiResponse<List<Member>>> getLiftedMembers(@PathVariable String chittiId) {
        List<Member> members = memberService.getLiftedMembers(chittiId);
        return ResponseEntity.ok(ApiResponse.success(members));
    }

    @ApiOperation(value = "Get non-lifted members of a chitty")
    @GetMapping("/chitty/{chittiId}/non-lifted")
    public ResponseEntity<ApiResponse<List<Member>>> getNonLiftedMembers(@PathVariable String chittiId) {
        List<Member> members = memberService.getNonLiftedMembers(chittiId);
        return ResponseEntity.ok(ApiResponse.success(members));
    }

    @ApiOperation(value = "Delete member")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteMember(@PathVariable String id) {
        if (memberService.deleteMember(id)) {
            return ResponseEntity.ok(ApiResponse.success("Member deleted successfully", "Deleted"));
        }
        return ResponseEntity.notFound().build();
    }
}