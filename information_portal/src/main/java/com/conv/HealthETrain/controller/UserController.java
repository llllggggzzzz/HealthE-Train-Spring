package com.conv.HealthETrain.controller;

import com.conv.HealthETrain.domain.User;
import com.conv.HealthETrain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public void loginByAccount(@RequestBody User loginUser) {
        userService.loginByAccount(loginUser);
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
    public void sendEmailCode(@RequestBody User loginUser) {
        userService.sendEmailCode(loginUser);
    }

    @PostMapping("/login/email/verify/{code}")
    public void verifyEmail(@RequestBody User loginUser, @RequestParam String code) {
        userService.verifyEmail(loginUser, code);
    }

    /**
     * 用户注册接口
     * @param registerUser
     */
    @PostMapping("/register")
    public void register(@RequestBody User registerUser) {
        userService.register(registerUser);
    }


    /**
     * @description 根据用户ID 查询用户信息，供其他服务进行openfeign调用
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public User getUserInfo(@PathVariable("id") Long id) {
        return userService.getById(id);
    }
}
