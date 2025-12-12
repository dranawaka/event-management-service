package com.aurelius.tech.eventmanagementservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class EventServiceRequest {
    
    @NotNull(message = "Service type ID is required")
    private UUID serviceTypeId;
    
    @NotNull(message = "Vendor ID is required")
    private UUID vendorId;
    
    @NotNull(message = "Rate is required")
    @Min(value = 0, message = "Rate must be non-negative")
    private BigDecimal rate;
    
    private String notes; // Optional notes or custom requirements
}






