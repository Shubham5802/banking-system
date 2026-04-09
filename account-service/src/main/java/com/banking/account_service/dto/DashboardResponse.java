package com.banking.account_service.dto;

import com.banking.account_service.entity.Account;
import lombok.Data;

import java.util.List;

@Data
public class DashboardResponse {
    private String userName;
    private String email;
    private List<Account> accounts;
    private Double totalBalanace;
}
