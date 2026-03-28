package com.banking.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NonNull;

@Data
public class CreateUser {
    @NotBlank
    private String name;

    @Email
    private String mail;

    private String password;
}
