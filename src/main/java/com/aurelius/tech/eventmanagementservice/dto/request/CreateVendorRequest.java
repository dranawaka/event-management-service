package com.aurelius.tech.eventmanagementservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateVendorRequest {
    
    @NotBlank(message = "Vendor name is required")
    private String name;
    
    private String description;
    
    @NotBlank(message = "Contact email is required")
    @Email(message = "Contact email should be valid")
    private String contactEmail;
    
    @NotBlank(message = "Contact phone is required")
    private String contactPhone;
    
    @NotNull(message = "Service type ID is required")
    private UUID serviceTypeId;
    
    @Min(value = 0, message = "Base rate must be non-negative")
    private BigDecimal baseRate;
    
    private Boolean isActive = true;
}






