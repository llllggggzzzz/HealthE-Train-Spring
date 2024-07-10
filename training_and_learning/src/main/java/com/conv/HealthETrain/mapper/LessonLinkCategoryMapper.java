package com.conv.HealthETrain.mapper;

import com.conv.HealthETrain.domain.POJP.LessonLinkCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
* @author flora
* @description 针对表【lesson_link_category】的数据库操作Mapper
* @createDate 2024-07-07 11:52:45
* @Entity com.conv.HealthETrain.domain.POJP.LessonLinkCategory
*/
public interface LessonLinkCategoryMapper extends BaseMapper<LessonLinkCategory> {

    // 根据传入的选修还是必修的类型统计学生总数
    @Select("SELECT COUNT(*) FROM lesson_link_category llc " +
            "INNER JOIN lesson l ON llc.lesson_id = l.lesson_id " +
            "WHERE l.lesson_type = #{lessonType}")
    int countStudentsByLessonType(@Param("lessonType") int lessonType);

}




