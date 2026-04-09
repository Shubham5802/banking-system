package com.banking.transaction_service.service.impl;

import com.banking.transaction_service.client.AccountClient;
import com.banking.transaction_service.dto.TransactionResponse;
import com.banking.transaction_service.dto.TransferRequest;
import com.banking.transaction_service.entity.Account;
import com.banking.transaction_service.entity.Transaction;
import com.banking.transaction_service.entity.TransactionTypes;
import com.banking.transaction_service.kafka.TransactionEventProducer;
import com.banking.transaction_service.repository.TransactionRepo;
import com.banking.transaction_service.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    TransactionRepo transactionRepo;

    @Autowired
    TransactionEventProducer producer;

    @Autowired
    AccountClient accountClient;

    @Override
    public TransactionResponse initiateTransfer(TransferRequest request) {
        // validate both accounts exist
        Account fromAccount=accountClient.getAccount(request.getFromAccountNumber());
        Account toAccount=accountClient.getAccount(request.getToAccountNumber());

        if(fromAccount.getBalance().doubleValue()< request.getAmount()){
            throw new RuntimeException("Insufficient Balance");
        }

        // save transaction as PENDING
        Transaction transaction=new Transaction();
        transaction.setFromAccountId(request.getFromAccountNumber());
        transaction.setToAccountId(request.getToAccountNumber());
        transaction.setAmount(request.getAmount());
        transaction.setType(TransactionTypes.TRANSFER);
        transaction.setStatus("PENDING");
        transactionRepo.save(transaction);

        // kick off saga — publish debit request
        String debitMsg="transactionId="+ transaction.getId()
                + ",fromAccount=" + request.getFromAccountNumber()
                + ",amount=" + request.getAmount();

        producer.publishDebitRequest(debitMsg);

        return mapToResponse(transaction);

    }

    @Override
    public List<TransactionResponse> getTransactionHistory(String accountNumber) {
        List<Transaction> sent = transactionRepo.findByFromAccountId(accountNumber);
        List<Transaction> received = transactionRepo.findByToAccountId(accountNumber);

        sent.addAll(received);
        return sent.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private TransactionResponse mapToResponse(Transaction t) {
        TransactionResponse response = new TransactionResponse();
        response.setId(t.getId());
        response.setFromAccountNumber(t.getFromAccountId());
        response.setToAccountNumber(t.getToAccountId());
        response.setAmount(t.getAmount());
        response.setType(t.getType());
        response.setStatus(t.getStatus());
        response.setTimestamp(t.getTimestamp());
        return response;
    }
}
