package com.conv.HealthETrain.controller;

import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.ExamQuestionService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/examQuestion")
public class ExamQuestionController {

    private ExamQuestionService examQuestionService;

    // 根据题型来统计五类题型的数量
    @GetMapping("/statistic")
    public ApiResponse<Map<String, Integer>> getExamQuestionsStatistic() {
        return ApiResponse.success(ResponseCode.SUCCEED,"成功",examQuestionService.countExamQuestionsByType());
    }
}
