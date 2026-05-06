package com.banking.transaction_service.strategy;

public class CurrentFeeStrategy implements FeeCalculationStrategy{

    @Override
    public Double calculateFee(Double amount) {
        return amount * 0.005;
    }
}
