package com.banking.transaction_service.client;

import com.banking.transaction_service.entity.Account;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "account-service", url = "${account.service.url}")
public interface AccountClient {

    @GetMapping("/api/accounts/account/{accountNumber}")
    Account getAccount(@PathVariable String accountNumber);
}
