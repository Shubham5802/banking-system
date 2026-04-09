package com.banking.transaction_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TransferRequest {

    @NotNull
    private String fromAccountNumber;

    @NotNull
    private String toAccountNumber;

    @NotNull
    @Positive
    private Double amount;

}
