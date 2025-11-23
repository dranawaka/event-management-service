package com.aurelius.tech.eventmanagementservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private String accessToken;
    private String refreshToken;
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
}
