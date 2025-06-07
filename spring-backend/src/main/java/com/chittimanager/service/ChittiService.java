package com.chittimanager.service;

import com.chittimanager.model.Chitty;
import com.chittimanager.model.Member;
import com.chittimanager.repository.ChittiRepository;
import com.chittimanager.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChittiService {

    @Autowired
    private ChittiRepository chittiRepository;

    @Autowired
    private MemberRepository memberRepository;

    public List<Chitty> getAllActiveChitties() {
        return chittiRepository.findByIsActiveTrue();
    }

    public Optional<Chitty> getChittiById(String id) {
        return chittiRepository.findById(id);
    }

    public Chitty createChitty(Chitty chitty) {
        chitty.setStartDate(LocalDateTime.now());
        return chittiRepository.save(chitty);
    }

    public Chitty updateChitty(String id, Chitty chittyDetails) {
        Optional<Chitty> existingChitty = chittiRepository.findById(id);
        if (existingChitty.isPresent()) {
            Chitty chitty = existingChitty.get();
            chitty.setName(chittyDetails.getName());
            chitty.setAmount(chittyDetails.getAmount());
            chitty.setTotalMembers(chittyDetails.getTotalMembers());
            chitty.setTotalMonths(chittyDetails.getTotalMonths());
            chitty.setUpdatedAt(LocalDateTime.now());
            return chittiRepository.save(chitty);
        }
        return null;
    }

    public boolean deleteChitty(String id) {
        if (chittiRepository.existsById(id)) {
            // Soft delete by setting isActive to false
            Optional<Chitty> chitty = chittiRepository.findById(id);
            if (chitty.isPresent()) {
                chitty.get().setActive(false);
                chittiRepository.save(chitty.get());
                return true;
            }
        }
        return false;
    }

    public Chitty addMemberToChitty(String chittiId, String memberName) {
        Optional<Chitty> chittyOpt = chittiRepository.findById(chittiId);
        if (chittyOpt.isPresent()) {
            Chitty chitty = chittyOpt.get();
            
            // Create new member
            Member member = new Member(memberName, chittiId);
            Member savedMember = memberRepository.save(member);
            
            // Add member ID to chitty
            chitty.getMemberIds().add(savedMember.getId());
            return chittiRepository.save(chitty);
        }
        return null;
    }

    public boolean removeMemberFromChitty(String chittiId, String memberId) {
        Optional<Chitty> chittyOpt = chittiRepository.findById(chittiId);
        if (chittyOpt.isPresent()) {
            Chitty chitty = chittyOpt.get();
            chitty.getMemberIds().remove(memberId);
            chittiRepository.save(chitty);
            
            // Delete member
            memberRepository.deleteById(memberId);
            return true;
        }
        return false;
    }

    public List<Chitty> searchChitties(String searchTerm) {
        return chittiRepository.findByNameContainingIgnoreCase(searchTerm);
    }
}