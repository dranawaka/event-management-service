package com.aurelius.tech.eventmanagementservice.controller;

import com.aurelius.tech.eventmanagementservice.dto.request.CreateRegistrationRequest;
import com.aurelius.tech.eventmanagementservice.entity.Registration;
import com.aurelius.tech.eventmanagementservice.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/registrations")
public class RegistrationController {
    
    private final RegistrationService registrationService;
    
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }
    
    @GetMapping
    public ResponseEntity<List<Registration>> getUserRegistrations(@RequestParam UUID userId) {
        return ResponseEntity.ok(registrationService.getRegistrationsByUserId(userId));
    }
    
    @PostMapping
    public ResponseEntity<Registration> createRegistration(
            @Valid @RequestBody CreateRegistrationRequest request,
            @RequestParam UUID userId) {
        Registration registration = registrationService.createRegistration(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(registration);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Registration> getRegistrationById(@PathVariable UUID id) {
        return ResponseEntity.ok(registrationService.getRegistrationById(id));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelRegistration(@PathVariable UUID id) {
        registrationService.cancelRegistration(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/qr")
    public ResponseEntity<String> getQRCode(@PathVariable UUID id) {
        Registration registration = registrationService.getRegistrationById(id);
        return ResponseEntity.ok(registration.getQrCode());
    }
}

