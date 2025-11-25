package com.aurelius.tech.eventmanagementservice.controller;

import com.aurelius.tech.eventmanagementservice.dto.response.ServiceTypeResponse;
import com.aurelius.tech.eventmanagementservice.service.ServiceTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/service-types")
public class ServiceTypeController {
    
    private final ServiceTypeService serviceTypeService;
    
    public ServiceTypeController(ServiceTypeService serviceTypeService) {
        this.serviceTypeService = serviceTypeService;
    }
    
    @GetMapping
    public ResponseEntity<List<ServiceTypeResponse>> getAllServiceTypes() {
        return ResponseEntity.ok(serviceTypeService.getAllServiceTypes());
    }
}



