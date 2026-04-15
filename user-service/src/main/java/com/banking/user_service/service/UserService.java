package com.banking.user_service.service;

import com.banking.user_service.dto.CreateUser;
import com.banking.user_service.dto.LoginRequest;
import com.banking.user_service.entity.Users;
import org.springframework.stereotype.Service;

import java.util.List;


public interface UserService {
        public String save(CreateUser user);
        public String login(LoginRequest request);
        public Users getUser(Integer id);

        public List<Users> getAllUsers();
}
