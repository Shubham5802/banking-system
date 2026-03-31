package com.banking.account_service.controller;

import com.banking.account_service.dto.CreateAccountRequest;
import com.banking.account_service.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



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


}
