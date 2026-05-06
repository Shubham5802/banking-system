package com.banking.transaction_service.strategy;

public class SavingsFeeStrategy implements FeeCalculationStrategy{

    @Override
    public Double calculateFee(Double amount) {
        return amount * 0.001;
    }
}
