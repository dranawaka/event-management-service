package com.aurelius.tech.eventmanagementservice.service;

import com.aurelius.tech.eventmanagementservice.dto.response.ServiceTypeResponse;
import com.aurelius.tech.eventmanagementservice.entity.ServiceType;
import com.aurelius.tech.eventmanagementservice.exception.ResourceNotFoundException;
import com.aurelius.tech.eventmanagementservice.repository.ServiceTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ServiceTypeService {
    
    private final ServiceTypeRepository serviceTypeRepository;
    
    public ServiceTypeService(ServiceTypeRepository serviceTypeRepository) {
        this.serviceTypeRepository = serviceTypeRepository;
    }
    
    public ServiceType getServiceTypeById(UUID id) {
        return serviceTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceType", "id", id));
    }
    
    public List<ServiceTypeResponse> getAllServiceTypes() {
        return serviceTypeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    private ServiceTypeResponse mapToResponse(ServiceType serviceType) {
        ServiceTypeResponse response = new ServiceTypeResponse();
        response.setId(serviceType.getId());
        response.setName(serviceType.getName());
        response.setDescription(serviceType.getDescription());
        response.setCreatedAt(serviceType.getCreatedAt());
        return response;
    }
}






