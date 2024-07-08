package com.conv.HealthETrain.controller;

import com.conv.HealthETrain.domain.User;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.UserService;
import lombok.AllArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/userList")
@AllArgsConstructor
public class UserListController {
    private final UserService userService;

    @GetMapping("")
    public ApiResponse<List<User>> getAllUsers(){
        List<User> userList = userService.getAllUsers();
        return ApiResponse.success(userList);
    }
}
