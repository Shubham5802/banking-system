package com.banking.transaction_service.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Account {
    private Integer id;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private Integer userId;
}
