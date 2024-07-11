package com.conv.HealthETrain.mapper;

import com.conv.HealthETrain.domain.DTO.LessonSimpleInfoDTO;
import com.conv.HealthETrain.domain.POJP.Lesson;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
* @author flora
* @description 针对表【lesson】的数据库操作Mapper
* @createDate 2024-07-07 11:52:45
* @Entity com.conv.HealthETrain.domain.POJP.Lesson
*/
public interface LessonMapper extends BaseMapper<Lesson> {
    // 获得所有课程的基本信息
    @Select("SELECT lesson_id as lessonId, lesson_name as lessonName, lesson_cover as lessonCover FROM lesson")
    List<LessonSimpleInfoDTO> getAllLessonSimpleInfo();

    // 获取所有课程全部信息
    @Select("SELECT * FROM lesson")
    List<Lesson> findAllLessons();

    // 根据课程id获取categoryId
    @Select("SELECT category_id FROM lesson_link_category WHERE lesson_id = #{lessonId}")
    List<Long> findCategoriesByLessonId(@Param("lessonId") Long lessonId);

    // 根据课程Id查询section总数量
    @Select("SELECT l.lesson_id, COUNT(s.section_id) AS total_sections " +
            "FROM lesson l " +
            "INNER JOIN chapter c ON l.lesson_id = c.lesson_id " +
            "INNER JOIN section s ON c.chapter_id = s.chapter_id " +
            "WHERE l.lesson_type = 1  AND l.lesson_id = #{lessonId}" +
            "GROUP BY l.lesson_id")
    List<Map<String, Object>> getTotalSectionsFromLesson(@Param("lessonId") long lessonId);
}




