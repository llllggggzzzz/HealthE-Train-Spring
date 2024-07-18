package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.Courseware;
import com.conv.HealthETrain.service.CoursewareService;
import com.conv.HealthETrain.mapper.CoursewareMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
* @author flora
* @description 针对表【courseware】的数据库操作Service实现
* @createDate 2024-07-07 11:52:15
*/
@Service
@RequiredArgsConstructor
public class CoursewareServiceImpl extends ServiceImpl<CoursewareMapper, Courseware>
    implements CoursewareService{

    private final CoursewareMapper coursewareMapper;

    @Override
    public Courseware getCoursewareBySectionId(Long sectionId) {
        return lambdaQuery().eq(Courseware::getSectionId, sectionId).one();
    }
}




