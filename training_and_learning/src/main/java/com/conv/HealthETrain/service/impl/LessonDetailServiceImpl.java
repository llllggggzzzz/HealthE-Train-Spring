package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.POJP.LessonDetail;
import com.conv.HealthETrain.mapper.LessonDetailMapper;
import com.conv.HealthETrain.service.LessonDetailService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author liusg
 */
@Service
@RequiredArgsConstructor
public class LessonDetailServiceImpl extends ServiceImpl<LessonDetailMapper, LessonDetail>
    implements LessonDetailService {

    private final LessonDetailMapper lessonDetailMapper;

    @Override
    public LessonDetail getByLessonId(Long lessonId) {
        LambdaQueryWrapper<LessonDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(LessonDetail::getLessonId, lessonId);
        return lessonDetailMapper.selectOne(lambdaQueryWrapper);
    }
}
