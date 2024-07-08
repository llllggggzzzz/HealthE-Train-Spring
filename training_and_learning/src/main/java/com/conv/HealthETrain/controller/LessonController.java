package com.conv.HealthETrain.controller;


import com.conv.HealthETrain.domain.Lesson;
import com.conv.HealthETrain.domain.LessonLinkCategory;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.LessonLinkCategoryService;
import com.conv.HealthETrain.service.LessonService;
import lombok.AllArgsConstructor;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lesson")
@AllArgsConstructor
public class LessonController {

    private final LessonLinkCategoryService lessonLinkCategoryService;


    @GetMapping("/user/{category_id}")
    public ApiResponse<List<LessonLinkCategory>> getLessons(@PathVariable("category_id") Long category_id) {
        List<LessonLinkCategory> lessons = lessonLinkCategoryService.getAllLessonsByCategoryId(category_id);
        if (lessons != null) return  ApiResponse.success(lessons);
        else return ApiResponse.success();
    }
}
