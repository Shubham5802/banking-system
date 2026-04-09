package com.banking.account_service.kafka;

import com.banking.account_service.entity.Account;
import com.banking.account_service.repository.AccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class SagaEventConsumer {

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private AccountEventProducer producer;

    @KafkaListener(topics = "debit-request",groupId = "account-service-group")
    public void handleDebitRequest(String message){
        // message: "transactionId=123,fromAccount=ABC,amount=500.0"
        try{
            String transactionId=extract(message,"transactionId");
            String accountNumber = extract(message, "fromAccount");
            BigDecimal amount = new BigDecimal(extract(message, "amount"));

            Account account=accountRepo.findByAccountNumber(accountNumber);

            if(account==null || account.getBalance().compareTo(amount)<0){
                producer.publishEvent("debit-failed","transactionId="+transactionId);
                return;
            }

            account.setBalance(account.getBalance().subtract(amount));
            accountRepo.save(account);
            producer.publishEvent("debit-success", "transactionId=" + transactionId);
        }catch (Exception e){
            String transactionId=extract(message, "transactionId");
            producer.publishEvent("debit-failed","transactionId="+transactionId);
        }
    }

    @KafkaListener(topics = "credit-request", groupId = "account-service-group")
    public void handleCreditRequest(String message) {
        // message: "transactionId=123,toAccount=XYZ,amount=500.0"
        try {
            String transactionId = extract(message, "transactionId");
            String accountNumber = extract(message, "toAccount");
            BigDecimal amount = new BigDecimal(extract(message, "amount"));

            Account account = accountRepo.findByAccountNumber(accountNumber);

            if (account == null) {
                producer.publishEvent("credit-failed", "transactionId=" + transactionId);
                return;
            }

            account.setBalance(account.getBalance().add(amount));
            accountRepo.save(account);
            producer.publishEvent("credit-success", "transactionId=" + transactionId);

        } catch (Exception e) {
            String transactionId = extract(message, "transactionId");
            producer.publishEvent("credit-failed", "transactionId=" + transactionId);
        }
    }

    @KafkaListener(topics = "compensate-debit", groupId = "account-service-group")
    public void handleCompensateDebit(String message) {
        // message: "transactionId=123,fromAccount=ABC,amount=500.0"
        try {
            String accountNumber = extract(message, "fromAccount");
            BigDecimal amount = new BigDecimal(extract(message, "amount"));

            Account account = accountRepo.findByAccountNumber(accountNumber);
            if (account != null) {
                account.setBalance(account.getBalance().add(amount));
                accountRepo.save(account);
            }
        } catch (Exception e) {
            // log compensation failure — needs manual intervention in real systems
            System.err.println("Compensation failed: " + message);
        }
    }

    private String extract(String message, String key) {
        for (String part : message.split(",")) {
            if (part.startsWith(key + "=")) {
                return part.split("=", 2)[1];
            }
        }
        throw new RuntimeException("Key not found in message: " + key);
    }
}
