package com.banking.account_service.service.impl;

import com.banking.account_service.client.UserClient;
import com.banking.account_service.dto.CreateAccountRequest;
import com.banking.account_service.entity.Account;
import com.banking.account_service.kafka.AccountEventProducer;
import com.banking.account_service.repository.AccountRepo;
import com.banking.account_service.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepo accountRepo;

    @Autowired
    UserClient userClient;

    @Autowired
    AccountEventProducer accountEventProducer;

    @Override
    public String createAccount(CreateAccountRequest request) {

        userClient.getUserById(request.getUserId());

        Account account = new Account();
        account.setAccountNumber(UUID.randomUUID()
                .toString()
                .substring(0, 10)
                .toUpperCase()
        );
        account.setBalance(request.getInitialDeposit());
        account.setUserId(request.getUserId());
        account.setAccountType(request.getAccountType());
        accountRepo.save(account);

        accountEventProducer.publishAccountCreated(
                "userId=" + request.getUserId() + ", account=" + account.getAccountNumber()
        );

        return "account created";
    }
}
