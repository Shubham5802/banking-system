package com.banking.transaction_service.strategy;

import org.springframework.stereotype.Component;

@Component
public class FeeStrategyProvider {

    public FeeCalculationStrategy getStrategy(String accountType){
        return switch (accountType.toUpperCase()){
          case "CURRENT" -> new CurrentFeeStrategy();
          default -> new SavingsFeeStrategy();
        };
    }
}
