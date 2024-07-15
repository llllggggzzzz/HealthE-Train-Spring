package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.DTO.LessonBrowseDTO;
import com.conv.HealthETrain.domain.DTO.LessonCategoryInfoDTO;
import com.conv.HealthETrain.domain.DTO.LessonSimpleInfoDTO;
import com.conv.HealthETrain.domain.POJP.Lesson;
import com.conv.HealthETrain.domain.POJP.LessonDetail;
import com.conv.HealthETrain.domain.POJP.LessonLinkCategory;
import com.conv.HealthETrain.domain.POJP.Star;
import com.conv.HealthETrain.mapper.LessonDetailMapper;
import com.conv.HealthETrain.mapper.LessonLinkCategoryMapper;
import com.conv.HealthETrain.mapper.StarMapper;
import com.conv.HealthETrain.service.LessonService;
import com.conv.HealthETrain.mapper.LessonMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author flora
* @description 针对表【lesson】的数据库操作Service实现
* @createDate 2024-07-07 11:52:45
*/
@Service
@AllArgsConstructor
public class LessonServiceImpl extends ServiceImpl<LessonMapper, Lesson>
    implements LessonService{

    private final LessonMapper lessonMapper;
    private final LessonDetailMapper lessonDetailMapper;
    private final LessonLinkCategoryMapper lessonLinkCategoryMapper;
    private final StarMapper starMapper;
    // 获取所有课程的基本信息
    @Override
    public List<LessonSimpleInfoDTO> getAllSimpleInfo(){
        return lessonMapper.getAllLessonSimpleInfo();
    }

    // 获取本课程所有信息+课程类别列表
    @Override
    public List<LessonCategoryInfoDTO> getLessonCategoryInfo() {
        List<Lesson> lessons = lessonMapper.findAllLessons();
        return lessons.stream()
                .map(lesson -> {
                    List<Long> categoryList = lessonMapper.findCategoriesByLessonId(lesson.getLessonId());
                    LessonCategoryInfoDTO dto = new LessonCategoryInfoDTO();
                    dto.setLessonId(lesson.getLessonId());
                    dto.setLessonName(lesson.getLessonName());
                    dto.setLessonType(lesson.getLessonType());
                    dto.setStartTime(lesson.getStartTime());
                    dto.setEndTime(lesson.getEndTime());
                    dto.setLessonCover(lesson.getLessonCover());
                    dto.setCategoryList(lessonMapper.findCategoriesByLessonId(lesson.getLessonId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateLessonType(Long lessonId, Integer lessonType) {
        Lesson lesson = new Lesson();
        lesson.setLessonType(lessonType);
        UpdateWrapper<Lesson> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("lesson_id", lessonId);
        int rows = lessonMapper.update(lesson, updateWrapper);
        return rows > 0;
    }

    @Override
    public List<LessonBrowseDTO> searchLessons(String searchString) {
        DecimalFormat df = new DecimalFormat("#.#");
        // 拿到所有课程
        List<Lesson> lessons = lessonMapper.selectList(
                new QueryWrapper<Lesson>().like("lesson_name", searchString)
        );
        return lessons.stream().map(lesson -> {
            // 详细信息
            LessonDetail lessonDetail = lessonDetailMapper.selectOne(
                    new QueryWrapper<LessonDetail>().eq("lesson_id", lesson.getLessonId())
            );
            // 平均评分
            double averageScore = starMapper.selectObjs(
                            new QueryWrapper<Star>().select("score").eq("lesson_id", lesson.getLessonId())
                    ).stream()
                    .mapToDouble(score -> ((Number) score).doubleValue()) // 将任意 Number 类型转换为 double
                    .average()
                    .orElse(0.0);

            averageScore = Double.parseDouble(df.format(averageScore));
            // 课程对应的所有category
            List<Long> categoryIds = lessonLinkCategoryMapper.selectList(
                    new QueryWrapper<LessonLinkCategory>().eq("lesson_id", lesson.getLessonId())
            ).stream().map(LessonLinkCategory::getCategoryId).collect(Collectors.toList());

            LessonBrowseDTO dto = new LessonBrowseDTO();
            dto.setLessonId(lesson.getLessonId());
            dto.setLessonName(lesson.getLessonName());
            dto.setStartTime(lesson.getStartTime());
            dto.setEndTime(lesson.getEndTime());
            dto.setLessonCover(lesson.getLessonCover());
            dto.setCategoryList(categoryIds);
            dto.setStar(averageScore);
            if (lessonDetail != null) {
                dto.setObjects(lessonDetail.getLessonObject());
            }
            return dto;
        }).collect(Collectors.toList());
    }

}





