package com.aurelius.tech.eventmanagementservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "venues")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venue {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String address;
    
    @Column(nullable = false)
    private String city;
    
    private String state;
    
    @Column(nullable = false)
    private String country;
    
    @Column(name = "zip_code")
    private String zipCode;
    
    @Column(nullable = false)
    private Integer capacity;
    
    private BigDecimal latitude;
    
    private BigDecimal longitude;
}









