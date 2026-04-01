package com.banking.user_service.service.impl;

import com.banking.user_service.dto.CreateUser;
import com.banking.user_service.dto.LoginRequest;
import com.banking.user_service.entity.Users;
import com.banking.user_service.repository.UserRepo;
import com.banking.user_service.service.UserService;
import com.banking.user_service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceimpl implements UserService{

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    UserRepo userRepo;

    @Autowired
    PasswordEncoder passwordEncoder;


    @Override
    public String save(CreateUser user) {
        if(userRepo.existsByMail(user.getMail())){
            return "user already exists";
        }else{
        Users userCurrent=new Users();
        userCurrent.setMail(user.getMail());
        userCurrent.setName(user.getName());
        userCurrent.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(userCurrent);
        return "User created";
        }
    }

    @Override
    public String login(LoginRequest request) {
        Users user=userRepo.findByMail(request.getMail())
                .orElseThrow(()->new RuntimeException("user not found"));

        if(!passwordEncoder.matches(request.getPassword(),user.getPassword())){
            throw new RuntimeException("Invalid Password");
        }
        return jwtUtil.generateToken(request.getMail());
    }

    @Override
    @Cacheable(value = "users", key = "#id")
    public Users getUser(Integer id) {
        System.out.println("Fetching user from DB for id: " + id);
        return userRepo.findById(id)
                .orElseThrow(()->new RuntimeException("User not found"));
    }
}
