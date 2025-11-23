package com.aurelius.tech.eventmanagementservice.repository;

import com.aurelius.tech.eventmanagementservice.entity.Payment;
import com.aurelius.tech.eventmanagementservice.entity.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByRegistrationId(UUID registrationId);
    List<Payment> findAllByRegistrationId(UUID registrationId);
    List<Payment> findByTransactionId(String transactionId);
    List<Payment> findByStatus(PaymentStatus status);
    List<Payment> findAllByRegistration_EventIdAndStatus(UUID eventId, PaymentStatus status);
}

