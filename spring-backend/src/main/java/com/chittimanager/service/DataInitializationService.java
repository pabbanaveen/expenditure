package com.chittimanager.service;

import com.chittimanager.model.Chitty;
import com.chittimanager.model.Member;
import com.chittimanager.repository.ChittiRepository;
import com.chittimanager.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class DataInitializationService implements CommandLineRunner {

    @Autowired
    private ChittiRepository chittiRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (chittiRepository.count() > 0) {
            System.out.println("Data already exists, skipping initialization");
            return;
        }

        System.out.println("Initializing mock data...");

        // Create 5 Lakh chitty with 20 members for 20 months
        Chitty fiveLakhChitty = new Chitty("5 Lakh Chitty", 500000.0, 20, 20);
        fiveLakhChitty.setStartDate(LocalDateTime.now());
        Chitty savedChitty = chittiRepository.save(fiveLakhChitty);

        // Create 20 mock members
        List<String> memberNames = Arrays.asList(
                "Rajesh Kumar", "Priya Sharma", "Amit Singh", "Sunita Devi", "Vikash Gupta",
                "Meera Joshi", "Suresh Reddy", "Kavita Patel", "Ravi Kumar", "Anjali Singh",
                "Manoj Yadav", "Sita Ram", "Deepak Verma", "Radha Krishna", "Santosh Kumar",
                "Gita Devi", "Rakesh Jain", "Shanti Bai", "Mukesh Agarwal", "Kamala Devi"
        );

        for (String name : memberNames) {
            Member member = new Member(name, savedChitty.getId());
            Member savedMember = memberRepository.save(member);
            savedChitty.getMemberIds().add(savedMember.getId());
        }

        // Update chitty with member IDs
        chittiRepository.save(savedChitty);

        System.out.println("Mock data initialized successfully!");
        System.out.println("Created chitty: " + savedChitty.getName());
        System.out.println("Total members: " + savedChitty.getMemberIds().size());
        System.out.println("Regular payment: ₹" + savedChitty.getRegularPayment());
        System.out.println("Lifted payment: ₹" + savedChitty.getLiftedPayment());
    }
}