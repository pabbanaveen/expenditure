package com.chittimanager.service;

import com.chittimanager.model.Chitty;
import com.chittimanager.model.Member;
import com.chittimanager.model.MonthlySlip;
import com.chittimanager.repository.MonthlySlipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MonthlySlipService {

    @Autowired
    private MonthlySlipRepository monthlySlipRepository;

    @Autowired
    private ChittiService chittiService;

    @Autowired
    private MemberService memberService;

    public List<MonthlySlip> getSlipsByChittiId(String chittiId) {
        return monthlySlipRepository.findByChittiIdOrderByMonthAsc(chittiId);
    }

    public Optional<MonthlySlip> getSlipByChittiIdAndMonth(String chittiId, int month) {
        return monthlySlipRepository.findByChittiIdAndMonth(chittiId, month);
    }

    public MonthlySlip generateMonthlySlip(String chittiId, int month) {
        // Check if slip already exists
        Optional<MonthlySlip> existingSlip = monthlySlipRepository.findByChittiIdAndMonth(chittiId, month);
        if (existingSlip.isPresent()) {
            return existingSlip.get();
        }

        // Get chitty details
        Optional<Chitty> chittyOpt = chittiService.getChittiById(chittiId);
        if (!chittyOpt.isPresent()) {
            return null;
        }

        Chitty chitty = chittyOpt.get();
        List<Member> members = memberService.getMembersByChittiId(chittiId);

        // Create new monthly slip
        MonthlySlip monthlySlip = new MonthlySlip(chittiId, month);

        // Add payment records for all members
        for (Member member : members) {
            double amount = member.isHasLifted() ? chitty.getLiftedPayment() : chitty.getRegularPayment();
            MonthlySlip.PaymentRecord record = new MonthlySlip.PaymentRecord(
                    member.getId(),
                    member.getName(),
                    amount,
                    member.isHasLifted()
            );
            monthlySlip.getPaymentRecords().add(record);
        }

        return monthlySlipRepository.save(monthlySlip);
    }

    public MonthlySlip markMemberAsLifted(String slipId, String memberId) {
        Optional<MonthlySlip> slipOpt = monthlySlipRepository.findById(slipId);
        if (slipOpt.isPresent()) {
            MonthlySlip slip = slipOpt.get();
            
            // Mark member as lifted in member service
            memberService.markMemberAsLifted(memberId, slip.getMonth());
            
            // Update slip
            slip.setLiftedMemberId(memberId);
            slip.setUpdatedAt(LocalDateTime.now());
            
            // Update payment records to reflect lifted status
            Optional<Chitty> chittyOpt = chittiService.getChittiById(slip.getChittiId());
            if (chittyOpt.isPresent()) {
                Chitty chitty = chittyOpt.get();
                for (MonthlySlip.PaymentRecord record : slip.getPaymentRecords()) {
                    if (record.getMemberId().equals(memberId)) {
                        record.setLifted(true);
                        record.setAmount(chitty.getLiftedPayment());
                        break;
                    }
                }
            }
            
            return monthlySlipRepository.save(slip);
        }
        return null;
    }

    public MonthlySlip markPayment(String slipId, String memberId, boolean isPaid) {
        Optional<MonthlySlip> slipOpt = monthlySlipRepository.findById(slipId);
        if (slipOpt.isPresent()) {
            MonthlySlip slip = slipOpt.get();
            
            for (MonthlySlip.PaymentRecord record : slip.getPaymentRecords()) {
                if (record.getMemberId().equals(memberId)) {
                    record.setPaid(isPaid);
                    break;
                }
            }
            
            slip.setUpdatedAt(LocalDateTime.now());
            return monthlySlipRepository.save(slip);
        }
        return null;
    }

    public boolean deleteSlip(String id) {
        if (monthlySlipRepository.existsById(id)) {
            monthlySlipRepository.deleteById(id);
            return true;
        }
        return false;
    }
}