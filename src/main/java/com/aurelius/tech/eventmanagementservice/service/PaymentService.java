package com.aurelius.tech.eventmanagementservice.service;

import com.aurelius.tech.eventmanagementservice.entity.Payment;
import com.aurelius.tech.eventmanagementservice.entity.Registration;
import com.aurelius.tech.eventmanagementservice.entity.enums.PaymentStatus;
import com.aurelius.tech.eventmanagementservice.exception.BusinessException;
import com.aurelius.tech.eventmanagementservice.exception.ResourceNotFoundException;
import com.aurelius.tech.eventmanagementservice.repository.PaymentRepository;
import com.aurelius.tech.eventmanagementservice.repository.RegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final RegistrationRepository registrationRepository;
    
    public PaymentService(PaymentRepository paymentRepository, RegistrationRepository registrationRepository) {
        this.paymentRepository = paymentRepository;
        this.registrationRepository = registrationRepository;
    }
    
    @Transactional
    public Payment processPayment(UUID registrationId, String paymentMethod, String transactionId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration", "id", registrationId));
        
        if (registration.getTotalAmount() == null || registration.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Invalid payment amount");
        }
        
        Payment payment = new Payment();
        payment.setRegistrationId(registrationId);
        payment.setAmount(registration.getTotalAmount());
        payment.setCurrency("USD");
        payment.setPaymentMethod(paymentMethod);
        payment.setTransactionId(transactionId);
        payment.setStatus(PaymentStatus.SUCCESS);
        
        payment = paymentRepository.save(payment);
        
        registration.setStatus(com.aurelius.tech.eventmanagementservice.entity.enums.RegistrationStatus.CONFIRMED);
        registrationRepository.save(registration);
        
        return payment;
    }
    
    public Payment getPaymentById(UUID id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
    }
    
    public List<Payment> getPaymentHistory(UUID registrationId) {
        return paymentRepository.findAllByRegistrationId(registrationId);
    }
    
    @Transactional
    public Payment processRefund(UUID paymentId) {
        Payment payment = getPaymentById(paymentId);
        
        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new BusinessException("Only successful payments can be refunded");
        }
        
        UUID registrationId = payment.getRegistrationId();
        payment.setStatus(PaymentStatus.REFUNDED);
        Payment savedPayment = paymentRepository.save(payment);
        
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration", "id", registrationId));
        registration.setStatus(com.aurelius.tech.eventmanagementservice.entity.enums.RegistrationStatus.CANCELLED);
        registrationRepository.save(registration);
        
        return savedPayment;
    }
}

