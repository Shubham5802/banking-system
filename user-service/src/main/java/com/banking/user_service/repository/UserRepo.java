package com.banking.user_service.repository;

import com.banking.user_service.entity.Users;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<Users,Integer> {

    Optional<Users> findByMail(String mail);

    boolean existsByMail(String mail);
}
