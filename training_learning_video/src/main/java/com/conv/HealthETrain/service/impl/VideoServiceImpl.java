package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.Video;
import com.conv.HealthETrain.service.VideoService;
import com.conv.HealthETrain.mapper.VideoMapper;
import org.springframework.stereotype.Service;

/**
* @author flora
* @description 针对表【video】的数据库操作Service实现
* @createDate 2024-07-07 11:53:19
*/
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video>
    implements VideoService{

    @Override
    public Video getVideoBySectionId(Long sectionId) {
        return lambdaQuery().eq(Video::getSectionId, sectionId).one();
    }
}




