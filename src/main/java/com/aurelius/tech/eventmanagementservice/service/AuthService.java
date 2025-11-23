package com.aurelius.tech.eventmanagementservice.service;

import com.aurelius.tech.eventmanagementservice.dto.request.LoginRequest;
import com.aurelius.tech.eventmanagementservice.dto.request.RegisterRequest;
import com.aurelius.tech.eventmanagementservice.dto.response.AuthResponse;
import com.aurelius.tech.eventmanagementservice.entity.User;
import com.aurelius.tech.eventmanagementservice.entity.enums.UserRole;
import com.aurelius.tech.eventmanagementservice.entity.enums.UserStatus;
import com.aurelius.tech.eventmanagementservice.exception.BusinessException;
import com.aurelius.tech.eventmanagementservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setRole(UserRole.ATTENDEE);
        user.setStatus(UserStatus.ACTIVE);
        
        user = userRepository.save(user);
        
        return new AuthResponse(null, null, user.getId(), user.getEmail(), 
                user.getFirstName(), user.getLastName(), user.getRole().name());
    }
    
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Invalid email or password"));
        
        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("Invalid email or password");
        }
        
        return new AuthResponse(null, null, user.getId(), user.getEmail(),
                user.getFirstName(), user.getLastName(), user.getRole().name());
    }
}

