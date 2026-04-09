package com.banking.account_service.dto;

import com.banking.account_service.entity.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateAccountRequest {


    private Integer userId;


    private AccountType accountType;

    @NotNull
    private BigDecimal amount;

}
