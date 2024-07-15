package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.POJP.LessonLinkTeacher;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author flora
* @description 针对表【lesson_link_teacher】的数据库操作Service
* @createDate 2024-07-07 11:52:45
*/
public interface LessonLinkTeacherService extends IService<LessonLinkTeacher> {

     List<Long> getTeachersByLessonId(Long lessonIds);

     List<Long> getLessonsByTeacherId(Long tdId);
}
