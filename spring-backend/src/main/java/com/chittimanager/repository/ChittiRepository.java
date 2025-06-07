package com.chittimanager.repository;

import com.chittimanager.model.Chitty;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChittiRepository extends MongoRepository<Chitty, String> {
    List<Chitty> findByIsActiveTrue();
    List<Chitty> findByNameContainingIgnoreCase(String name);
}