package com.banking.account_service.client;

import com.banking.account_service.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="user-service",url="${user.service.url}")
public interface UserClient {
    @GetMapping("/api/users/{id}")
    UserDto getUserById(@PathVariable Integer id);

}
