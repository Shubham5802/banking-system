package com.banking.account_service.controller;

import com.banking.account_service.dto.CreateAccountRequest;
import com.banking.account_service.dto.DashboardResponse;
import com.banking.account_service.dto.UpdateAccountRequest;
import com.banking.account_service.entity.Account;
import com.banking.account_service.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    @Autowired
    AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<String> createAccount(@Valid @RequestBody CreateAccountRequest request){
        String status= accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(status);
    }

    @GetMapping("/health")
    public String health(){
        return "account running";
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Account>>  getAllAccounts(@PathVariable Integer userId){
        List<Account> accounts=accountService.getAllAccounts(userId);
        return  ResponseEntity.ok(accounts);
    }

    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<DashboardResponse> getAccounts(@PathVariable Integer userId){
        DashboardResponse response=accountService.getAccounts(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<Account> getAccount(@PathVariable String accountNumber){
        Account account=accountService.getAccount(accountNumber);
        return ResponseEntity.ok().body(account);
    }

    @PutMapping("/account/{accountNumber}/update-balance")
    public ResponseEntity<Account> updateAccount(@Valid
                                                     @RequestBody UpdateAccountRequest
                                                             updateAccountRequest,
                                                           @PathVariable String accountNumber){
        Account account=accountService.updateAccount(updateAccountRequest,accountNumber);
        return ResponseEntity.ok().body(account);
    }

}
