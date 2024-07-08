package com.conv.HealthETrain.controller;

import com.conv.HealthETrain.domain.User;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @author liusg
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    /**
     * 用户登录接口
     * @param loginUser
     */
    @PostMapping("/login/account")
    public ApiResponse<HashMap<String, Object>> loginByAccount(@RequestBody User loginUser) {
        String token = userService.loginByAccount(loginUser);
        HashMap<String, Object> data = new HashMap<>();
        data.put("token", token);

        return ApiResponse.success(data);
    }

    /**
     * 用户登录接口
     * @param loginUser
     */
    @PostMapping("/login/phone")
    public void loginByPhone(@RequestBody User loginUser) {
        userService.loginByPhone(loginUser);
    }

    /**
     * 邮箱用户登录接口
     * @param loginUser
     */
    @PostMapping("/login/email/code")
    public ApiResponse<Object> sendEmailCode(@RequestBody User loginUser) {
        userService.sendEmailCode(loginUser);
        return ApiResponse.success();
    }

    @PostMapping("/login/email/verify/{code}")
    public ApiResponse<Boolean> verifyEmail(@RequestBody User loginUser, @PathVariable("code") String code) {
        boolean result = userService.verifyEmail(loginUser, code);
        if (result) {
            return ApiResponse.success();
        }

        return ApiResponse.error(ResponseCode.BAD_REQUEST);
    }

    /**
     * 用户注册接口
     * @param registerUser
     */
    @PostMapping("/register")
    public ApiResponse<Object> register(@RequestBody User registerUser) {
        userService.register(registerUser);

        return ApiResponse.success();
    }
}
