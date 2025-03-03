package com.conv.HealthETrain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.conv.HealthETrain.domain.POJP.LessonAnnouncement;

import java.util.List;

/**
 * @author liusg
 */
public interface LessonAnnouncementService extends IService<LessonAnnouncement> {
    List<LessonAnnouncement> getLessonAnnouncements(Long lessonId);
}
