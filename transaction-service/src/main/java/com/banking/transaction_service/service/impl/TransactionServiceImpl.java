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
import com.banking.transaction_service.strategy.FeeCalculationStrategy;
import com.banking.transaction_service.strategy.FeeStrategyProvider;
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

    @Autowired
    FeeStrategyProvider feeStrategyProvider;

    @Override
    public TransactionResponse initiateTransfer(TransferRequest request) {
        // validate both accounts exist
        Account fromAccount=accountClient.getAccount(request.getFromAccountNumber());
        Account toAccount=accountClient.getAccount(request.getToAccountNumber());

        FeeCalculationStrategy strategy=feeStrategyProvider.getStrategy(fromAccount.getAccountType());
        Double fee= strategy.calculateFee(request.getAmount());

        if(fromAccount.getBalance().doubleValue()< request.getAmount()+fee){
            throw new RuntimeException("Insufficient Balance");
        }

        Transaction transaction=Transaction.builder()
                .fromAccountId(request.getFromAccountNumber())
                .toAccountId(request.getToAccountNumber())
                .amount(request.getAmount())
                .type(TransactionTypes.TRANSFER)
                .transactionFee(fee)
                .status("PENDING")
                .build();

        transactionRepo.save(transaction);

        // kick off saga — publish debit request
        double totalDebit=request.getAmount()+fee;
        String debitMsg="transactionId="+ transaction.getId()
                + ",fromAccount=" + request.getFromAccountNumber()
                + ",amount=" + totalDebit;

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
        response.setTransactionFee(t.getTransactionFee());
        return response;
    }
}
