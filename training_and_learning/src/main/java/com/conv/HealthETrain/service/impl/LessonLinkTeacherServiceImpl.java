package com.conv.HealthETrain.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.POJP.LessonLinkTeacher;
import com.conv.HealthETrain.service.LessonLinkTeacherService;
import com.conv.HealthETrain.mapper.LessonLinkTeacherMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
        return CollUtil.map(lessonLinkTeachers, LessonLinkTeacher::getLessonId, true);
    }
}




