package com.chittimanager.service;

import com.chittimanager.model.Member;
import com.chittimanager.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    public List<Member> getMembersByChittiId(String chittiId) {
        return memberRepository.findByChittiId(chittiId);
    }

    public Optional<Member> getMemberById(String id) {
        return memberRepository.findById(id);
    }

    public Member updateMember(String id, Member memberDetails) {
        Optional<Member> existingMember = memberRepository.findById(id);
        if (existingMember.isPresent()) {
            Member member = existingMember.get();
            member.setName(memberDetails.getName());
            member.setUpdatedAt(LocalDateTime.now());
            return memberRepository.save(member);
        }
        return null;
    }

    public Member markMemberAsLifted(String memberId, int month) {
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            member.setHasLifted(true);
            member.setLiftedDate(LocalDateTime.now());
            member.setLiftedMonth(month);
            return memberRepository.save(member);
        }
        return null;
    }

    public List<Member> getLiftedMembers(String chittiId) {
        return memberRepository.findByChittiIdAndHasLiftedTrue(chittiId);
    }

    public List<Member> getNonLiftedMembers(String chittiId) {
        return memberRepository.findByChittiIdAndHasLiftedFalse(chittiId);
    }

    public boolean deleteMember(String id) {
        if (memberRepository.existsById(id)) {
            memberRepository.deleteById(id);
            return true;
        }
        return false;
    }
}