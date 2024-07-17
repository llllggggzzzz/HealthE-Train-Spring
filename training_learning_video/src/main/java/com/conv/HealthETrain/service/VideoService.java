package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.Video;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author flora
* @description 针对表【video】的数据库操作Service
* @createDate 2024-07-07 11:53:19
*/
public interface VideoService extends IService<Video> {
    Video getVideoBySectionId(Long sectionId);
}
