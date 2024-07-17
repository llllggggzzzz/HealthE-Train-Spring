package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.DTO.LessonExamInfoDTO;
import com.conv.HealthETrain.domain.Exam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author flora
* @description 针对表【exam】的数据库操作Service
* @createDate 2024-07-07 11:47:43
*/
public interface ExamService extends IService<Exam> {

    List<LessonExamInfoDTO> getLessonExamInfo(Long lessonId, Long userId);
}
