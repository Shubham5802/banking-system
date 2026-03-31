package com.banking.account_service.service;

import com.banking.account_service.dto.CreateAccountRequest;

public interface AccountService {
    String createAccount(CreateAccountRequest request);
}
