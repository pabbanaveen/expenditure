package com.chittimanager.repository;

import com.chittimanager.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    List<Payment> findByChittiId(String chittiId);
    List<Payment> findByChittiIdAndMonth(String chittiId, int month);
    List<Payment> findByMemberId(String memberId);
    List<Payment> findByChittiIdAndMemberIdAndMonth(String chittiId, String memberId, int month);
}