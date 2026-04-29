package com.banking.account_service.factory;

import com.banking.account_service.dto.CreateAccountRequest;
import com.banking.account_service.entity.Account;
import com.banking.account_service.entity.AccountType;

import java.math.BigDecimal;
import java.util.UUID;

public class CurrentAccountFactory implements AccountFactory{

    @Override
    public Account createAccount(CreateAccountRequest request) {
        Account account=new Account();
        account.setAccountNumber(UUID.randomUUID().toString().substring(0,10).toUpperCase());
        account.setUserId(request.getUserId());
        account.setBalance(request.getInitialDeposit());
        account.setAccountType(AccountType.CURRENT);
        account.setMinimumBalance(BigDecimal.ZERO);
        account.setOverdraftLimit(new BigDecimal("10000"));
        return account;
    }
}
