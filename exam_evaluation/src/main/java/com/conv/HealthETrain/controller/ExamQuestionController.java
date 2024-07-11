package com.conv.HealthETrain.controller;

import com.conv.HealthETrain.domain.DTO.ExamQuestionStatisticDTO;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.ExamQuestionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    // 查询对应题目的试题(简单信息,非完整)
    @GetMapping("/questionBank/{id}/simpleInfo")
    private ApiResponse<List<ExamQuestionStatisticDTO>> getExamQuestionsByQuestionBankId(@PathVariable("id") Long qbId){
        List<ExamQuestionStatisticDTO> examQuestionStatisticDTOS = examQuestionService.getExamQuestionsStatisticByQbId(qbId);
        return ApiResponse.success(ResponseCode.SUCCEED,"成功",examQuestionStatisticDTOS);
    }

    // 批量删除题库里的试题,body里面携带试题id列表
    @DeleteMapping("/questionBank/{id}")
    private ApiResponse<Object> batchDeleteExamQuestions(@PathVariable("id") Long qbId,
                                                         @RequestBody List<Long> eqIds){
        examQuestionService.batchDeleteExamQuestions(qbId,eqIds);
        return ApiResponse.success(ResponseCode.SUCCEED,"成功");
    }
}
