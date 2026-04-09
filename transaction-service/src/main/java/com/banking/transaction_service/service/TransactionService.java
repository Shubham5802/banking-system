package com.banking.transaction_service.service;


import com.banking.transaction_service.dto.TransactionResponse;
import com.banking.transaction_service.dto.TransferRequest;

import java.util.List;

public interface TransactionService {
    TransactionResponse initiateTransfer(TransferRequest request);

    List<TransactionResponse> getTransactionHistory(String accountNumber);
}
