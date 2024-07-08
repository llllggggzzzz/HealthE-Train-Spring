package com.conv.HealthETrain.client;


import com.conv.HealthETrain.domain.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(value = "information_portal")
public interface UserClient {

    @GetMapping("/api/v1/userList")
    List<User> getAllUsers();
}
