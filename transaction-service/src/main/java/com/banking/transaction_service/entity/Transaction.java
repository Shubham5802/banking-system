package com.banking.transaction_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class Transaction {
    @Id
    @GeneratedValue
    private Integer id;

    @NotNull
    private String fromAccountId;

    @NotNull
    private String toAccountId;

    @NotNull
    private Double amount;

    @Enumerated(EnumType.STRING)
    private TransactionTypes type;

    private String status;

    @CreatedDate
    private LocalDateTime timestamp = LocalDateTime.now();
}
