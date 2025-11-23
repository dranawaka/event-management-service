package com.aurelius.tech.eventmanagementservice.service;

import com.aurelius.tech.eventmanagementservice.dto.response.VendorResponse;
import com.aurelius.tech.eventmanagementservice.entity.Vendor;
import com.aurelius.tech.eventmanagementservice.exception.ResourceNotFoundException;
import com.aurelius.tech.eventmanagementservice.repository.VendorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VendorService {
    
    private final VendorRepository vendorRepository;
    
    public VendorService(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }
    
    public Vendor getVendorById(UUID id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", id));
    }
    
    public List<VendorResponse> getVendorsByServiceType(UUID serviceTypeId) {
        List<Vendor> vendors = vendorRepository.findByServiceTypeIdAndIsActiveTrue(serviceTypeId);
        return vendors.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<VendorResponse> getAllActiveVendors() {
        return vendorRepository.findAll().stream()
                .filter(Vendor::getIsActive)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    private VendorResponse mapToResponse(Vendor vendor) {
        VendorResponse response = new VendorResponse();
        response.setId(vendor.getId());
        response.setName(vendor.getName());
        response.setDescription(vendor.getDescription());
        response.setContactEmail(vendor.getContactEmail());
        response.setContactPhone(vendor.getContactPhone());
        response.setServiceTypeId(vendor.getServiceTypeId());
        if (vendor.getServiceType() != null) {
            response.setServiceTypeName(vendor.getServiceType().getName());
        }
        response.setBaseRate(vendor.getBaseRate());
        response.setIsActive(vendor.getIsActive());
        response.setCreatedAt(vendor.getCreatedAt());
        return response;
    }
}

