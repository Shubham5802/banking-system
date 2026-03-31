package com.banking.account_service.repository;

import com.banking.account_service.entity.Account;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepo extends JpaRepository<Account,Integer> {
    List<Account> findByUserId(Integer userId);

}
