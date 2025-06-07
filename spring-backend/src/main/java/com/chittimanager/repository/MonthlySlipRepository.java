package com.chittimanager.repository;

import com.chittimanager.model.MonthlySlip;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonthlySlipRepository extends MongoRepository<MonthlySlip, String> {
    List<MonthlySlip> findByChittiId(String chittiId);
    Optional<MonthlySlip> findByChittiIdAndMonth(String chittiId, int month);
    List<MonthlySlip> findByChittiIdOrderByMonthAsc(String chittiId);
}