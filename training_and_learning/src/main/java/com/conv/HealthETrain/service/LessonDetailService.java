package com.conv.HealthETrain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.conv.HealthETrain.domain.POJP.LessonDetail;

/**
 * @author liusg
 */
public interface LessonDetailService extends IService<LessonDetail> {
    LessonDetail getByLessonId(Long lessonId);
}
