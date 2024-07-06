package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.LessonLinkCategory;
import com.conv.HealthETrain.enums.ExceptionCode;
import com.conv.HealthETrain.exception.GlobalException;
import com.conv.HealthETrain.service.LessonLinkCategoryService;
import com.conv.HealthETrain.mapper.LessonLinkCategoryMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author john
* @description 针对表【lesson_link_category】的数据库操作Service实现
* @createDate 2024-07-05 17:58:44
*/
@Service
@AllArgsConstructor
public class LessonLinkCategoryServiceImpl extends ServiceImpl<LessonLinkCategoryMapper, LessonLinkCategory>
    implements LessonLinkCategoryService{

    private final LessonLinkCategoryMapper lessonLinkCategoryMapper;

    @Override
    public List<LessonLinkCategory> getAllLessonsByCategoryId(Long categoryId) {
        LambdaQueryWrapper<LessonLinkCategory> lessonLinkCategoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        lessonLinkCategoryLambdaQueryWrapper.eq(LessonLinkCategory::getCategoryId, categoryId);
        List<LessonLinkCategory> lessons = lessonLinkCategoryMapper.selectList(lessonLinkCategoryLambdaQueryWrapper);
        if (lessons == null) {
            throw  new GlobalException("未查询到任何课程", ExceptionCode.NOT_FOUND);
        } else {
            return lessons;
        }
    }
}




