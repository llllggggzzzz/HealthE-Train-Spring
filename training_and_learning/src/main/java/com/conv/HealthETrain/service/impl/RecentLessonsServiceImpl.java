package com.conv.HealthETrain.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.POJP.RecentLessons;
import com.conv.HealthETrain.service.RecentLessonsService;
import com.conv.HealthETrain.mapper.RecentLessonsMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author flora
* @description 针对表【recent_lessons】的数据库操作Service实现
* @createDate 2024-07-07 11:52:45
*/
@Service
@AllArgsConstructor
public class RecentLessonsServiceImpl extends ServiceImpl<RecentLessonsMapper, RecentLessons>
    implements RecentLessonsService{

    private final RecentLessonsMapper recentLessonsMapper;


    @Override
    public List<Long> getLessonIdsByUserId(Long id) {
        LambdaQueryWrapper<RecentLessons> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(RecentLessons::getUserId, id).select(RecentLessons::getLessonId);
        List<RecentLessons> recentLessons = recentLessonsMapper.selectList(lambdaQueryWrapper);
        return CollUtil.map(recentLessons, RecentLessons::getLessonId, true);

    }
}




