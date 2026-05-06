package com.banking.account_service.factory;

import com.banking.account_service.entity.AccountType;
import org.springframework.stereotype.Component;

@Component
public class AccountFactoryProvider {

    public AccountFactory getFactory(AccountType type){
        System.out.println("AccountFactoryProvider");
        return switch (type){
            case SAVINGS -> new SavingsAccountFactory();
            case CURRENT -> new CurrentAccountFactory();
        };
    }
}
