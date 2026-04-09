package com.banking.account_service.service;

import com.banking.account_service.dto.CreateAccountRequest;
import com.banking.account_service.dto.DashboardResponse;
import com.banking.account_service.dto.UpdateAccountRequest;
import com.banking.account_service.entity.Account;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    String createAccount(CreateAccountRequest request);

    List<Account> getAllAccounts(Integer userId);

    DashboardResponse getAccounts(Integer userId);

    Account getAccount(String accountNumber);

    Account updateAccount(@Valid UpdateAccountRequest updateAccountRequest,
                                    String accountNumber);
}
