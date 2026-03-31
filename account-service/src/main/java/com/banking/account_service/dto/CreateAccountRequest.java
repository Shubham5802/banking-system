package com.banking.account_service.dto;

import com.banking.account_service.entity.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
public class CreateAccountRequest {

    @NotNull
    private Integer userId;

    @NotNull
    private AccountType accountType;

    @NotNull
    private BigDecimal initialDeposit;

}
