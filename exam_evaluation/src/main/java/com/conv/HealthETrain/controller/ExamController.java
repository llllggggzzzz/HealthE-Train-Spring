package com.conv.HealthETrain.controller;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.conv.HealthETrain.client.InformationPortalClient;
import com.conv.HealthETrain.client.LessonClient;
import com.conv.HealthETrain.domain.DTO.ExamDTO;
import com.conv.HealthETrain.domain.DTO.LessonExamInfoDTO;
import com.conv.HealthETrain.domain.DTO.LessonInfoDTO;
import com.conv.HealthETrain.domain.Exam;
import com.conv.HealthETrain.domain.Paper;
import com.conv.HealthETrain.domain.TeacherDetail;
import com.conv.HealthETrain.domain.User;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.ExamService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/exam")
@AllArgsConstructor
@Slf4j
public class ExamController {

    private final LessonClient lessonClient;

    private final ExamService examService ;

    private final InformationPortalClient informationPortalClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private static final ObjectMapper mapper = new ObjectMapper();

    @GetMapping("/{userId}")
    public ApiResponse<List<ExamDTO>> getExamInfo(@PathVariable("userId") Long userId) {
        ApiResponse<List<LessonInfoDTO>> lessonsResponse = lessonClient.getLessons(userId);
        List<ExamDTO> examDTOList = new ArrayList<>();
        if(lessonsResponse != null) {
            List<LessonInfoDTO> lessons = lessonsResponse.getData();
            List<Long> lessonIds = CollUtil.getFieldValues(lessons, "lessonId", Long.class);
            LambdaQueryWrapper<Exam> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.in(Exam::getLessionId, lessonIds);
            List<Exam> examList = examService.list(lambdaQueryWrapper);
            if(examList != null) {
                // 遍历examList, 查询teacherName和teacherCover
                for (Exam exam : examList) {
                    ExamDTO examDTO = new ExamDTO(exam);
                    Long creatorId = exam.getCreatorId();
                    TeacherDetail teacherDetail = informationPortalClient.getTeacherDetailById(creatorId);
                    if(teacherDetail != null) {
                        examDTO.setTeacherName(teacherDetail.getRealName());
                        Long teacherDetailUserId = teacherDetail.getUserId();
                        User userInfo = informationPortalClient.getUserInfo(teacherDetailUserId);
                        if(userInfo != null) {
                            examDTO.setTeacherCover(userInfo.getCover());
                        }
                    }
                    examDTOList.add(examDTO);
                }
            }

        }

        return ApiResponse.success(examDTOList);
    }

    @PostMapping("/add")
    public ApiResponse<String> addExam(@RequestBody Exam exam){
        boolean flag = examService.save(exam);
        if(flag){
            return ApiResponse.success(ResponseCode.SUCCEED,"成功");
        }
        else
        {
            return ApiResponse.error(ResponseCode.NOT_IMPLEMENTED,"失败");
        }
    }

    @GetMapping("/information/{lessonId}/{userId}")
    public ApiResponse<List<LessonExamInfoDTO>> getLessonExamInfos(@PathVariable("lessonId") Long lessonId,
                                                                   @PathVariable("userId")Long userId) throws JsonProcessingException {
        String redisKey = "examLessonInfo:"+lessonId+":"+userId;
        String cachedData = stringRedisTemplate.opsForValue().get(redisKey);
        if (cachedData != null) {
            List<LessonExamInfoDTO> cachedExamLesson = mapper.readValue(cachedData, mapper.getTypeFactory().constructCollectionType(List.class, LessonExamInfoDTO.class));
            return ApiResponse.success(ResponseCode.SUCCEED, "成功", cachedExamLesson);
        } else {
            List<LessonExamInfoDTO> lessonExamInfoDTOS = examService.getLessonExamInfo(lessonId,userId);
            String jsonExamLesson = mapper.writeValueAsString(lessonExamInfoDTOS);
            stringRedisTemplate.opsForValue().set(redisKey, jsonExamLesson);
            stringRedisTemplate.expire(redisKey, 10, TimeUnit.MINUTES);
            return ApiResponse.success(ResponseCode.SUCCEED, "成功", lessonExamInfoDTOS);
        }
    }

}
