package com.banking.transaction_service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void publishDebitRequest(String message){
        kafkaTemplate.send("debit-request",message);
    }

    public void publishCreditRequest(String message){
        kafkaTemplate.send("credit-request",message);
    }

    public void publishCompensateDebit(String message){
        kafkaTemplate.send("compensate-debit", message);
    }
}
