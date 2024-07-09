package com.conv.HealthETrain.controller;


import com.conv.HealthETrain.domain.TeacherDetail;
import com.conv.HealthETrain.service.TeacherDetailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/teacherdetial")
@Slf4j
public class TeacherDetailController {

    private final TeacherDetailService teacherDetailService;

    @GetMapping("/{id}")
    public TeacherDetail getTeacherDetailById(@PathVariable("id") Long id) {
        log.info("发生调用");
        return teacherDetailService.getById(id);
    }

}
