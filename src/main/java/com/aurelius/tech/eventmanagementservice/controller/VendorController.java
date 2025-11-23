package com.aurelius.tech.eventmanagementservice.controller;

import com.aurelius.tech.eventmanagementservice.dto.response.VendorResponse;
import com.aurelius.tech.eventmanagementservice.service.VendorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vendors")
public class VendorController {
    
    private final VendorService vendorService;
    
    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }
    
    @GetMapping("/service-type/{serviceTypeId}")
    public ResponseEntity<List<VendorResponse>> getVendorsByServiceType(@PathVariable UUID serviceTypeId) {
        return ResponseEntity.ok(vendorService.getVendorsByServiceType(serviceTypeId));
    }
    
    @GetMapping
    public ResponseEntity<List<VendorResponse>> getAllActiveVendors() {
        return ResponseEntity.ok(vendorService.getAllActiveVendors());
    }
}

