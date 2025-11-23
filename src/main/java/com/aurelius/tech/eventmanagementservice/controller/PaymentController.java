package com.aurelius.tech.eventmanagementservice.controller;

import com.aurelius.tech.eventmanagementservice.entity.Payment;
import com.aurelius.tech.eventmanagementservice.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    
    private final PaymentService paymentService;
    
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    @PostMapping
    public ResponseEntity<Payment> processPayment(@RequestBody Map<String, Object> request) {
        UUID registrationId = UUID.fromString((String) request.get("registrationId"));
        String paymentMethod = (String) request.get("paymentMethod");
        String transactionId = (String) request.get("transactionId");
        
        Payment payment = paymentService.processPayment(registrationId, paymentMethod, transactionId);
        return ResponseEntity.ok(payment);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }
    
    @PostMapping("/{id}/refund")
    public ResponseEntity<Payment> processRefund(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.processRefund(id));
    }
    
    @GetMapping("/history")
    public ResponseEntity<List<Payment>> getPaymentHistory(@RequestParam UUID registrationId) {
        return ResponseEntity.ok(paymentService.getPaymentHistory(registrationId));
    }
}


