package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.POJP.LessonAnnouncement;
import com.conv.HealthETrain.mapper.LessonAnnouncementMapper;
import com.conv.HealthETrain.service.LessonAnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liusg
 */
@Service
@RequiredArgsConstructor
public class LessonAnnouncementServiceImpl extends ServiceImpl<LessonAnnouncementMapper, LessonAnnouncement>
    implements LessonAnnouncementService {

    private final LessonAnnouncementMapper lessonAnnouncementMapper;

    @Override
    public List<LessonAnnouncement> getLessonAnnouncements(Long lessonId) {
        LambdaQueryWrapper<LessonAnnouncement> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(LessonAnnouncement::getLessonId, lessonId);
        return lessonAnnouncementMapper.selectList(lambdaQueryWrapper);
    }
}
