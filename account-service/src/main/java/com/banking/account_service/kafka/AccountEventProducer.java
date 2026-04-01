package com.banking.account_service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AccountEventProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void publishAccountCreated(String message){
        kafkaTemplate.send("account-created",message);
    }
}
