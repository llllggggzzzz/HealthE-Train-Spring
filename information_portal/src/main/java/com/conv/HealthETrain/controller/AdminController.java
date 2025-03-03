package com.conv.HealthETrain.controller;

import com.conv.HealthETrain.domain.Admin;
import com.conv.HealthETrain.domain.User;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admins")
public class AdminController {
    private final AdminService adminService;
    @PostMapping("/login/account")
    public ApiResponse<HashMap<String, Object>> loginByAccount(@RequestBody Admin loginAdmin) {
        String token = adminService.loginByAccountForAdmin(loginAdmin);
        HashMap<String, Object> data = new HashMap<>();
        data.put("token", token);
        return ApiResponse.success(data);
    }

    @PostMapping("/register")
    public ApiResponse<String> registerByAdmin(@RequestBody Admin registerAdmin){
        if (registerAdmin.getAccount() == null || registerAdmin.getPassword() == null) {
            return ApiResponse.error(ResponseCode.UNPROCESSABLE_ENTITY,"请填写好信息");
        }
        boolean registered = adminService.registerAdmin(registerAdmin);
        if (registered) {
            return ApiResponse.success(ResponseCode.SUCCEED,"成功");
        } else {
            return ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR,"注册失败");
        }
    }
}
