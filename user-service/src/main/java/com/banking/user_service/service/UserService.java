package com.banking.user_service.service;

import com.banking.user_service.dto.CreateUser;
import com.banking.user_service.entity.Users;
import org.springframework.stereotype.Service;


public interface UserService {
        public String save(CreateUser user);
}
