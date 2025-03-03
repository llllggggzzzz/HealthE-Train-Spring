package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.Courseware;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author flora
* @description 针对表【courseware】的数据库操作Service
* @createDate 2024-07-07 11:52:15
*/
public interface CoursewareService extends IService<Courseware> {

        Courseware getCoursewareBySectionId(Long sectionId);
}
