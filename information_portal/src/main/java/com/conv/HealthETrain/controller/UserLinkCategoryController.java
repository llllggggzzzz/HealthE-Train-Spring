package com.conv.HealthETrain.controller;

import com.conv.HealthETrain.domain.UserLinkCategory;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.UserLinkCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/userLinkCategory")
public class UserLinkCategoryController {
    private final UserLinkCategoryService userLinkCategoryService;

    // 删除指定的userId的出卷人权限
    @DeleteMapping("/{id}/authority")
    public ApiResponse<String> deleteUserAuthority(@PathVariable("id") Long userId){
        userLinkCategoryService.deleteUserLinkCategory(userId,8);
        return ApiResponse.success(ResponseCode.SUCCEED);
    }

    // 增加指定userId的出卷人权限
    @PutMapping("/{id}/authority")
    public ApiResponse<String> putUserAuthority(@PathVariable("id") Long userId){
        UserLinkCategory userLinkCategory = new UserLinkCategory();
        userLinkCategory.setUserId(userId);
        userLinkCategory.setCategoryId(8);
        userLinkCategoryService.save(userLinkCategory);
        return ApiResponse.success(ResponseCode.SUCCEED);
    }
}
