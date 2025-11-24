package com.aurelius.tech.eventmanagementservice.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ServiceTypeResponse {
    private UUID id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
}


