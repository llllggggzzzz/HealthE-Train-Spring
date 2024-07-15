package com.conv.HealthETrain.controller;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.conv.HealthETrain.client.InformationPortalClient;
import com.conv.HealthETrain.client.LessonClient;
import com.conv.HealthETrain.domain.DTO.ExamDTO;
import com.conv.HealthETrain.domain.DTO.LessonInfoDTO;
import com.conv.HealthETrain.domain.Exam;
import com.conv.HealthETrain.domain.TeacherDetail;
import com.conv.HealthETrain.domain.User;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.ExamService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/exam")
@AllArgsConstructor
@Slf4j
public class ExamController {

    private final LessonClient lessonClient;

    private final ExamService examService ;

    private final InformationPortalClient informationPortalClient;

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
}
