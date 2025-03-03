package com.conv.HealthETrain.client;


import com.conv.HealthETrain.domain.DTO.UserDTO;
import com.conv.HealthETrain.domain.TeacherDetail;
import com.conv.HealthETrain.domain.User;
import com.conv.HealthETrain.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("information-portal")
public interface InformationPortalClient {
    @GetMapping("/teacherdetial/{id}")
    TeacherDetail getTeacherDetailById(@PathVariable("id") Long id);

    @GetMapping("/users/{id}")
    User getUserInfo(@PathVariable("id") Long id);


    @GetMapping("/users/account/{account}")
    User getUserInfoByAccount(@PathVariable("account") String account);

    @GetMapping("/userList")
    List<User> getAllUsers();

    @GetMapping("/userList/{id}")
    User getUser(@PathVariable Long id);

    // 获取网站所有学生用户的信息
    @GetMapping("/users/students")
    List<User> getAllStudentsInfo();

    @GetMapping("/category/{categoryId}")
    String getCategoryNameById(@PathVariable("categoryId") Long categoryId);

    @GetMapping("/users/info/ids/{ids}")
    List<User> getUserByIds(@PathVariable("ids") String ids);

}
