package com.banking.account_service.service.impl;

import com.banking.account_service.client.UserClient;
import com.banking.account_service.dto.CreateAccountRequest;
import com.banking.account_service.dto.DashboardResponse;
import com.banking.account_service.dto.UpdateAccountRequest;
import com.banking.account_service.dto.UserDto;
import com.banking.account_service.entity.Account;
import com.banking.account_service.exception.ResourceNotFoundException;
import com.banking.account_service.kafka.AccountEventProducer;
import com.banking.account_service.repository.AccountRepo;
import com.banking.account_service.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
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

    @Override
    public List<Account> getAllAccounts(Integer userId) {
        userClient.getUserById(userId);

        return accountRepo.findByUserId(userId);
    }

    @Override
    public DashboardResponse getAccounts(Integer userId) {
        UserDto user=userClient.getUserById(userId);
        List<Account> accounts=accountRepo.findByUserId(userId);
        if(accounts.isEmpty()){
            throw new ResourceNotFoundException("No accounts for : "+userId);
        }
        DashboardResponse dashboardResponse=new DashboardResponse();
        dashboardResponse.setUserName(user.getName());
        dashboardResponse.setEmail(user.getMail());
        dashboardResponse.setAccounts(accounts);
        BigDecimal sum= BigDecimal.valueOf(0);
        for(Account acc:accounts){
            sum=sum.add(acc.getBalance());
        }
        dashboardResponse.setTotalBalanace(sum.doubleValue());
        return dashboardResponse;
    }

    @Override
    public Account getAccount(String accountNumber) {
        return accountRepo.findByAccountNumber(accountNumber);
    }

    @Override
    public Account updateAccount(UpdateAccountRequest updateAccountRequest,
                                           String accountNumber) {

        //Account account=accountRepo.findByAccountNumber(accountNumber);

        Account account = accountRepo.findByAccountNumber(accountNumber);
//                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));


        account.setAccountType(updateAccountRequest.getAccountType());
        account.setBalance(updateAccountRequest.getAmount());
        accountRepo.save(account);
        return account;
    }
}
