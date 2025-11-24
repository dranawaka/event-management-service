package com.aurelius.tech.eventmanagementservice.repository;

import com.aurelius.tech.eventmanagementservice.entity.User;
import com.aurelius.tech.eventmanagementservice.entity.enums.UserRole;
import com.aurelius.tech.eventmanagementservice.entity.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(UserRole role);
    List<User> findByStatus(UserStatus status);
    boolean existsByEmail(String email);
}





