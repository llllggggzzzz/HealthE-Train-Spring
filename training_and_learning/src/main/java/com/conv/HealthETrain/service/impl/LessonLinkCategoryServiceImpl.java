package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.POJP.Lesson;
import com.conv.HealthETrain.domain.POJP.LessonLinkCategory;
import com.conv.HealthETrain.mapper.LessonMapper;
import com.conv.HealthETrain.service.LessonLinkCategoryService;
import com.conv.HealthETrain.mapper.LessonLinkCategoryMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
}




