package com.aurelius.tech.eventmanagementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.aurelius.tech.eventmanagementservice.repository")
public class EventManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventManagementServiceApplication.class, args);
    }

}
