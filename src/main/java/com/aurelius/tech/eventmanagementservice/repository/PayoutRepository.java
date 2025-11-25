package com.aurelius.tech.eventmanagementservice.repository;

import com.aurelius.tech.eventmanagementservice.entity.Payout;
import com.aurelius.tech.eventmanagementservice.entity.enums.PayoutStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PayoutRepository extends JpaRepository<Payout, UUID> {
    List<Payout> findByOrganizerId(UUID organizerId);
    List<Payout> findByEventId(UUID eventId);
    List<Payout> findByStatus(PayoutStatus status);
}



