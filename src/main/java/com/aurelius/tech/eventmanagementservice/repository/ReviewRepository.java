package com.aurelius.tech.eventmanagementservice.repository;

import com.aurelius.tech.eventmanagementservice.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByEventId(UUID eventId);
    List<Review> findByEventIdAndIsApprovedTrue(UUID eventId);
    Optional<Review> findByUserIdAndEventId(UUID userId, UUID eventId);
    List<Review> findByIsApprovedFalse();
}









