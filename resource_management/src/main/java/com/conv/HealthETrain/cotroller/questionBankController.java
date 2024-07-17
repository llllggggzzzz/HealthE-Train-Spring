package com.conv.HealthETrain.cotroller;

import com.conv.HealthETrain.domain.DTO.QuestionBankSelectDTO;
import com.conv.HealthETrain.domain.QuestionBank;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.QuestionBankService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/questionBank")
public class questionBankController {
    private final QuestionBankService questionBankService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private static final ObjectMapper mapper = new ObjectMapper();
    @GetMapping("/all")
    List<QuestionBank> getAllQuestionBank() throws JsonProcessingException {
        String redisKey = "allQuestionBanks";
        String cachedData = stringRedisTemplate.opsForValue().get(redisKey);
        if (cachedData != null) {
            // 如果Redis中有缓存数据，直接返回缓存数据
            return mapper.readValue(cachedData, mapper.getTypeFactory().constructCollectionType(List.class, QuestionBank.class));
        } else {
            // 如果Redis中没有缓存数据，从数据库中获取数据
            List<QuestionBank> questionBanks = questionBankService.getAllQuestionBanks();
            // 将数据转换为JSON字符串并存入Redis
            stringRedisTemplate.opsForValue().set(redisKey, mapper.writeValueAsString(questionBanks));
            stringRedisTemplate.expire(redisKey, 10, TimeUnit.MINUTES);
            return questionBanks;
        }
    }

    @GetMapping("/lesson/{lessonId}")
    public ApiResponse<List<QuestionBankSelectDTO>> getQuestionBankInfoByLessonId(@PathVariable("lessonId")Long lessonId) throws JsonProcessingException {
        String redisKey = "lesson:" + lessonId + ":questionBankInfo";
        String cachedData = stringRedisTemplate.opsForValue().get(redisKey);
        if (cachedData != null) {
            List<QuestionBankSelectDTO> cachedQuestionBanks = mapper.readValue(cachedData, mapper.getTypeFactory().constructCollectionType(List.class, QuestionBankSelectDTO.class));
            return ApiResponse.success(ResponseCode.SUCCEED, "成功", cachedQuestionBanks);
        } else {
            List<QuestionBankSelectDTO> questionBankSelectDTOS = questionBankService.getQuestionBankSelectDTOByLessonId(lessonId);
            String jsonQuestionBanks = mapper.writeValueAsString(questionBankSelectDTOS);
            stringRedisTemplate.opsForValue().set(redisKey, jsonQuestionBanks);
            stringRedisTemplate.expire(redisKey, 10, TimeUnit.MINUTES);
            return ApiResponse.success(ResponseCode.SUCCEED, "成功", questionBankSelectDTOS);
        }
    }
}
