package com.aurelius.tech.eventmanagementservice.repository;

import com.aurelius.tech.eventmanagementservice.entity.Registration;
import com.aurelius.tech.eventmanagementservice.entity.enums.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, UUID> {
    List<Registration> findByUserId(UUID userId);
    List<Registration> findByEventId(UUID eventId);
    List<Registration> findByEventIdAndStatus(UUID eventId, RegistrationStatus status);
    Optional<Registration> findByQrCode(String qrCode);
    boolean existsByUserIdAndEventId(UUID userId, UUID eventId);
}





