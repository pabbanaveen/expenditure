package com.chittimanager.repository;

import com.chittimanager.model.Member;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends MongoRepository<Member, String> {
    List<Member> findByChittiId(String chittiId);
    List<Member> findByChittiIdAndHasLiftedTrue(String chittiId);
    List<Member> findByChittiIdAndHasLiftedFalse(String chittiId);
}