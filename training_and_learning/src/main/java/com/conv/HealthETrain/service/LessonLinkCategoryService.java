package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.POJP.LessonLinkCategory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
* @author flora
* @description 针对表【lesson_link_category】的数据库操作Service
* @createDate 2024-07-07 11:52:45
*/
public interface LessonLinkCategoryService extends IService<LessonLinkCategory> {

    // 根据传入的选修还是必修的类型统计学生总数
    int countStudentsByLessonType(int lessonType);

    // 根据传入的选修还是必修统计七类课程的数目
    Map<String, Integer> countCategoriesByLessonType(Integer lessonType);
}
