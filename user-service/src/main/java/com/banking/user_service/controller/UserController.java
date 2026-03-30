package com.banking.user_service.controller;

import com.banking.user_service.dto.CreateUser;
import com.banking.user_service.dto.LoginRequest;
import com.banking.user_service.entity.Users;
import com.banking.user_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> save(@RequestBody CreateUser user){
        String status=userService.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(status);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request){

        String token=userService.login(request);
        return ResponseEntity.ok(token);
    }
}
