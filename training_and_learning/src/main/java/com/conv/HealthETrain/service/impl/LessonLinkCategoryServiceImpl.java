package com.conv.HealthETrain.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.Lesson;
import com.conv.HealthETrain.domain.POJP.LessonLinkCategory;
import com.conv.HealthETrain.domain.POJP.LessonLinkTeacher;
import com.conv.HealthETrain.mapper.LessonMapper;
import com.conv.HealthETrain.service.LessonLinkCategoryService;
import com.conv.HealthETrain.mapper.LessonLinkCategoryMapper;
import lombok.AllArgsConstructor;
import org.apache.ibatis.executor.BatchResult;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author flora
* @description 针对表【lesson_link_category】的数据库操作Service实现
* @createDate 2024-07-07 11:52:45
*/
@Service
@AllArgsConstructor
public class LessonLinkCategoryServiceImpl extends ServiceImpl<LessonLinkCategoryMapper, LessonLinkCategory>
    implements LessonLinkCategoryService{

    private final LessonLinkCategoryMapper lessonLinkCategoryMapper;

    private final LessonMapper lessonMapper;

    // 根据传入的选修还是必修的类型统计学生总数
    @Override
    public int countStudentsByLessonType(int lessonType) {
        return lessonLinkCategoryMapper.countStudentsByLessonType(lessonType);
    }

    // 根据传入的选修还是必修统计七类课程的数目
    @Override
    public Map<String, Integer> countCategoriesByLessonType(Integer lessonType) {
        QueryWrapper<Lesson> lessonQueryWrapper = new QueryWrapper<>();
        lessonQueryWrapper.eq("lesson_type", lessonType);
        List<Lesson> lessons = lessonMapper.selectList(lessonQueryWrapper);

        if (lessons.isEmpty()) {
            return new HashMap<>();
        }

        List<Long> lessonIds = lessons.stream().map(Lesson::getLessonId).toList();

        // 查询对应的categoryId及其数量
        QueryWrapper<LessonLinkCategory> linkQueryWrapper = new QueryWrapper<>();
        linkQueryWrapper.in("lesson_id", lessonIds);
        linkQueryWrapper.select("category_id", "count(*) as count");
        linkQueryWrapper.groupBy("category_id");

        List<Map<String, Object>> results = lessonLinkCategoryMapper.selectMaps(linkQueryWrapper);
        // 转换结果为Map<String, Integer>
        Map<String, Integer> categoryCountMap = new HashMap<>();
        for (Map<String, Object> result : results) {
            String categoryId = result.get("category_id").toString();
            Integer count = Integer.parseInt(result.get("count").toString());
            categoryCountMap.put(categoryId, count);
        }
        return categoryCountMap;
    }

    @Override
    public List<Long> getCategoriesByLessonId(Long lessonId) {
        LambdaQueryWrapper<LessonLinkCategory> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(LessonLinkCategory::getLessonId, lessonId).select(LessonLinkCategory::getCategoryId);
        List<LessonLinkCategory> lessonLinkCategories = lessonLinkCategoryMapper.selectList(lambdaQueryWrapper);
        return CollUtil.getFieldValues(lessonLinkCategories, "categoryId", Long.class);
    }

    @Override
    public Boolean saveCategoriesByLessonId(Long lessonId, List<Long> categoryIds) {
        Collection<LessonLinkCategory> lessonLinkCategories = new HashSet<>();

        for (Long categoryId : categoryIds) {
            LessonLinkCategory lessonLinkCategory = new LessonLinkCategory();
            lessonLinkCategory.setLessonId(lessonId);
            lessonLinkCategory.setCategoryId(categoryId);

            lessonLinkCategories.add(lessonLinkCategory);
        }

        List<BatchResult> results = lessonLinkCategoryMapper.insert(lessonLinkCategories);

        for (BatchResult result : results) {
            int[] updateCounts = result.getUpdateCounts();
            for (int count : updateCounts) {
                if (count == 0) {
                    return false;
                }
            }
        }
        return true;
    }
}