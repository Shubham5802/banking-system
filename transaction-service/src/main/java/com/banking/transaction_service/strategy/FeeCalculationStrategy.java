package com.banking.transaction_service.strategy;

public interface FeeCalculationStrategy {
    Double calculateFee(Double amount);
}
