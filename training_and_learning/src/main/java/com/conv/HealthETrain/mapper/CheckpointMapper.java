package com.conv.HealthETrain.mapper;

import com.conv.HealthETrain.domain.Checkpoint;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author flora
* @description 针对表【checkpoint】的数据库操作Mapper
* @createDate 2024-07-07 11:52:45
* @Entity com.conv.HealthETrain.domain.Checkpoint
*/
public interface CheckpointMapper extends BaseMapper<Checkpoint> {
    // 根据课程chapterId查找学习总人数
    @Select("SELECT COUNT(DISTINCT user_id) FROM checkpoint WHERE chapter_id = #{chapterId}")
    int getUniqueUserCountByChapterId(@Param("chapterId") Long chapterId);
    // 根据课程ID，chapterId和UserId查学生学习进度
    @Select("SELECT * FROM checkpoint WHERE user_id = #{userId} AND lesson_id = #{lessonId} AND chapter_id = #{chapterId}")
    Checkpoint getCheckpointByUserAndLesson(@Param("userId") Long userId, @Param("lessonId") Long lessonId, @Param("chapterId") Long chapterId);
}




