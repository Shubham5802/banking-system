package com.banking.transaction_service.kafka;

import com.banking.transaction_service.entity.Transaction;
import com.banking.transaction_service.repository.TransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventConsumer {
    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private TransactionEventProducer producer;

    @KafkaListener(topics = "debit-success", groupId="transaction-service-group")
    public void handleDebitSuccess(String message){
        // message format: "transactionId=123"
        Integer transactionId=extractId(message);
        Transaction transaction=transactionRepo.findById(transactionId).orElseThrow();

        // debit succeeded, now request credit
        String creditMsg="transactionId="+transactionId
                +",toAccount="+ transaction.getToAccountId()
                +",amount="+ transaction.getAmount();

        producer.publishCreditRequest(creditMsg);
    }

    @KafkaListener(topics = "debit-failed",groupId = "transaction-service-group")
    public void handleDebitFailed(String message){
        Integer transactionId = extractId(message);
        Transaction transaction = transactionRepo.findById(transactionId).orElseThrow();
        transaction.setStatus("FAILED");
        transactionRepo.save(transaction);
    }

    @KafkaListener(topics = "credit-failed", groupId = "transaction-service-group")
    public void handleCreditFailed(String message) {
        Integer transactionId = extractId(message);
        Transaction transaction = transactionRepo.findById(transactionId).orElseThrow();
        transaction.setStatus("FAILED");
        transactionRepo.save(transaction);

        // compensate: refund the source account
        String compensateMsg = "transactionId=" + transactionId
                + ",fromAccount=" + transaction.getFromAccountId()
                + ",amount=" + transaction.getAmount();

        producer.publishCompensateDebit(compensateMsg);
    }


    private Integer extractId(String message) {
        // parses "transactionId=123,..." → 123
        String part = message.split(",")[0];
        return Integer.parseInt(part.split("=")[1]);
    }

}
