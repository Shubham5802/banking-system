package com.banking.user_service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AccountEventConsumer {
    @KafkaListener(topics="account-created", groupId="user-service-group")
    public void handleAccountCreated(String message){
        System.out.println("Account created event received: " + message);

    }
}
