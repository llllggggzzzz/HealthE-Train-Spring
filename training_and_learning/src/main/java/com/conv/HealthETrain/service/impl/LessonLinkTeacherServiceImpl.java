package com.conv.HealthETrain.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Console;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.DTO.LessonSelectDTO;
import com.conv.HealthETrain.domain.POJP.Lesson;
import com.conv.HealthETrain.domain.POJP.LessonLinkTeacher;
import com.conv.HealthETrain.mapper.LessonMapper;
import com.conv.HealthETrain.service.LessonLinkTeacherService;
import com.conv.HealthETrain.mapper.LessonLinkTeacherMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author flora
* @description 针对表【lesson_link_teacher】的数据库操作Service实现
* @createDate 2024-07-07 11:52:45
*/
@Service
@AllArgsConstructor
public class LessonLinkTeacherServiceImpl extends ServiceImpl<LessonLinkTeacherMapper, LessonLinkTeacher>
    implements LessonLinkTeacherService{

    private final LessonLinkTeacherMapper lessonLinkTeacherMapper;

    @Override
    public List<Long> getTeachersByLessonId(Long lessonId) {
        LambdaQueryWrapper<LessonLinkTeacher> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(LessonLinkTeacher::getLessonId, lessonId).select(LessonLinkTeacher::getTdId);
        List<LessonLinkTeacher> lessonLinkTeachers = lessonLinkTeacherMapper.selectList(lambdaQueryWrapper);
        return CollUtil.getFieldValues(lessonLinkTeachers, "tdId", Long.class);
    }

    @Override
    public List<LessonSelectDTO> getLessonSelectInfoByTdId(Long tdId) {
        List<LessonSelectDTO> lessonSelectDTOs = new ArrayList<>();

        QueryWrapper<LessonLinkTeacher> wrapper = new QueryWrapper<>();
        wrapper.eq("td_id", tdId);

        List<LessonLinkTeacher> lessonLinkTeachers = lessonLinkTeacherMapper.selectList(wrapper);
        // 遍历每个 LessonLinkTeacher，查询对应的 Lesson 信息并封装为 LessonSelectDTO
        for (LessonLinkTeacher lessonLinkTeacher : lessonLinkTeachers) {
            Lesson lesson = lessonMapper.selectById(lessonLinkTeacher.getLessonId());
            if (lesson != null) {
                LessonSelectDTO lessonSelectDTO = new LessonSelectDTO();
                lessonSelectDTO.setLessonId(lesson.getLessonId());
                lessonSelectDTO.setLessonName(lesson.getLessonName());
                lessonSelectDTOs.add(lessonSelectDTO);
            }
        }
        return lessonSelectDTOs;
    }

    @Override
    public List<Long> getLessonsByTeacherId(Long tdId) {
        LambdaQueryWrapper<LessonLinkTeacher> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(LessonLinkTeacher::getTdId, tdId).select(LessonLinkTeacher::getLessonId);
        List<LessonLinkTeacher> lessonLinkTeachers = lessonLinkTeacherMapper.selectList(lambdaQueryWrapper);
        return CollUtil.getFieldValues(lessonLinkTeachers, "lessonId", Long.class);
    }

}




