package com.conv.HealthETrain.client;


import com.conv.HealthETrain.domain.TeacherDetail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("information_portal")
public interface InformationPortalClient {
    @GetMapping("/teacherdetial/{id}")
    TeacherDetail getTeacherDetailById(@PathVariable("id") Long id);
}
