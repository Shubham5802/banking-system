package com.banking.user_service.service.impl;

import com.banking.user_service.dto.CreateUser;
import com.banking.user_service.entity.Users;
import com.banking.user_service.repository.UserRepo;
import com.banking.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceimpl implements UserService{
    @Autowired
    UserRepo userRepo;

    @Autowired
    PasswordEncoder passwordEncoder;


    @Override
    public String save(CreateUser user) {
        Users userCurrent=new Users();
        userCurrent.setMail(user.getMail());
        userCurrent.setName(user.getName());
        userCurrent.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(userCurrent);
        return "User created";
    }
}
