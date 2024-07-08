package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.POJP.Section;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author flora
* @description 针对表【section】的数据库操作Service
* @createDate 2024-07-07 11:52:45
*/
public interface SectionService extends IService<Section> {
    List<Section> getSectionsByChapterId(Long chapterId);
}
