package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.DTO.LessonCategoryInfoDTO;
import com.conv.HealthETrain.domain.DTO.LessonSimpleInfoDTO;
import com.conv.HealthETrain.domain.POJP.Lesson;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author flora
* @description 针对表【lesson】的数据库操作Service
* @createDate 2024-07-07 11:52:45
*/
public interface LessonService extends IService<Lesson> {
    // 获取所有课程的基本信息
    List<LessonSimpleInfoDTO> getAllSimpleInfo();
    // 获取课程详细信息+课程类别
    List<LessonCategoryInfoDTO> getLessonCategoryInfo();
    // 更新某项课程的选修必修
    boolean updateLessonType(Long lessonId, Integer lessonType);
}
