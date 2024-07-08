package com.conv.HealthETrain.controller;


import com.conv.HealthETrain.domain.TeacherDetail;
import com.conv.HealthETrain.service.TeacherDetailService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/teacherdetial")
public class TeacherDetailController {

    private final TeacherDetailService teacherDetailService;

    @GetMapping("/{id}")
    public TeacherDetail getTeacherDetailById(@PathVariable("id") Long id) {
        return teacherDetailService.getById(id);
    }

}
