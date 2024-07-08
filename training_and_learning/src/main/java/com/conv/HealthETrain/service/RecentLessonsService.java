package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.POJP.RecentLessons;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author flora
* @description 针对表【recent_lessons】的数据库操作Service
* @createDate 2024-07-07 11:52:45
*/
public interface RecentLessonsService extends IService<RecentLessons> {

    List<Long> getLessonIdsByUserId(Long id);
}
