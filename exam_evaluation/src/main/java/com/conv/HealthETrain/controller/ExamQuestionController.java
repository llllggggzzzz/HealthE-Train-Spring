package com.conv.HealthETrain.controller;

import com.conv.HealthETrain.domain.DTO.ExamQuestionStatisticDTO;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.ExamQuestionService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/examQuestion")
public class ExamQuestionController {

    private ExamQuestionService examQuestionService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private static final ObjectMapper mapper = new ObjectMapper();

    // 根据题型来统计五类题型的数量
    @GetMapping("/statistic")
    public ApiResponse<Map<String, Integer>> getExamQuestionsStatistic() throws JsonProcessingException {
        String cacheKey = "examQuestionsStatistic";
        String cachedData = stringRedisTemplate.opsForValue().get(cacheKey);
        if (cachedData != null) {
            Map<String, Integer> statistic = mapper.readValue(cachedData, Map.class);
            return ApiResponse.success(ResponseCode.SUCCEED, "成功", statistic);
        }
        Map<String, Integer> statistic = examQuestionService.countExamQuestionsByType();
        String jsonData = mapper.writeValueAsString(statistic);
        stringRedisTemplate.opsForValue().set(cacheKey, jsonData, 10, TimeUnit.MINUTES);
        return ApiResponse.success(ResponseCode.SUCCEED, "成功", statistic);
    }

    // 查询对应题目的试题(简单信息,非完整)
    @GetMapping("/questionBank/{id}/simpleInfo")
    private ApiResponse<List<ExamQuestionStatisticDTO>> getExamQuestionsByQuestionBankId(@PathVariable("id") Long qbId) throws JsonProcessingException {
        String redisKey = "questionBank:" + qbId + ":simpleInfo";
        String cachedData = stringRedisTemplate.opsForValue().get(redisKey);
        if (cachedData != null) {
            List<ExamQuestionStatisticDTO> cachedExamQuestions = mapper.readValue(cachedData, mapper.getTypeFactory().constructCollectionType(List.class, ExamQuestionStatisticDTO.class));
            return ApiResponse.success(ResponseCode.SUCCEED, "成功", cachedExamQuestions);
        } else {
            List<ExamQuestionStatisticDTO> examQuestionStatisticDTOS = examQuestionService.getExamQuestionsStatisticByQbId(qbId);
            String jsonExamQuestions = mapper.writeValueAsString(examQuestionStatisticDTOS);
            stringRedisTemplate.opsForValue().set(redisKey, jsonExamQuestions);
            stringRedisTemplate.expire(redisKey, 10, TimeUnit.MINUTES);
            return ApiResponse.success(ResponseCode.SUCCEED, "成功", examQuestionStatisticDTOS);
        }
    }

    // 批量删除题库里的试题,body里面携带试题id列表
    @DeleteMapping("/questionBank/{id}")
    private ApiResponse<Object> batchDeleteExamQuestions(@PathVariable("id") Long qbId,
                                                         @RequestBody List<Long> eqIds){
        examQuestionService.batchDeleteExamQuestions(qbId,eqIds);
        return ApiResponse.success(ResponseCode.SUCCEED,"成功");
    }
}
