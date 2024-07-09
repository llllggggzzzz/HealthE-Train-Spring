package com.conv.HealthETrain.client;


import com.conv.HealthETrain.domain.TeacherDetail;
import com.conv.HealthETrain.domain.User;
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

    @GetMapping("/userList")
    List<User> getAllUsers();
}
