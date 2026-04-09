package com.banking.transaction_service.dto;

import com.banking.transaction_service.entity.TransactionTypes;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionResponse {

    private Integer id;
    private String fromAccountNumber;
    private String toAccountNumber;
    private Double amount;
    private TransactionTypes type;
    private String status;
    private LocalDateTime timestamp;
}
