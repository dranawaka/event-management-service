package com.aurelius.tech.eventmanagementservice.controller;

import com.aurelius.tech.eventmanagementservice.dto.request.CreateVendorRequest;
import com.aurelius.tech.eventmanagementservice.dto.response.VendorResponse;
import com.aurelius.tech.eventmanagementservice.entity.Vendor;
import com.aurelius.tech.eventmanagementservice.service.VendorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    
    @PostMapping
    public ResponseEntity<VendorResponse> createVendor(@Valid @RequestBody CreateVendorRequest request) {
        Vendor vendor = vendorService.createVendor(request);
        VendorResponse response = vendorService.mapToResponse(vendor);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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

