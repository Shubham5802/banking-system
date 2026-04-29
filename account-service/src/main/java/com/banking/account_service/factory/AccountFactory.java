package com.banking.account_service.factory;

import com.banking.account_service.dto.CreateAccountRequest;
import com.banking.account_service.entity.Account;

public interface AccountFactory {
    Account createAccount(CreateAccountRequest request);
}
