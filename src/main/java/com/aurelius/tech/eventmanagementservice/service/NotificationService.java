package com.aurelius.tech.eventmanagementservice.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationService {
    
    private final RabbitTemplate rabbitTemplate;
    
    public NotificationService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    
    public void sendRegistrationConfirmation(String email, Map<String, Object> data) {
        Map<String, Object> message = Map.of(
                "type", "REGISTRATION_CONFIRMATION",
                "email", email,
                "data", data
        );
        rabbitTemplate.convertAndSend("notification.queue", message);
    }
    
    public void sendEventReminder(String email, Map<String, Object> data) {
        Map<String, Object> message = Map.of(
                "type", "EVENT_REMINDER",
                "email", email,
                "data", data
        );
        rabbitTemplate.convertAndSend("notification.queue", message);
    }
}





