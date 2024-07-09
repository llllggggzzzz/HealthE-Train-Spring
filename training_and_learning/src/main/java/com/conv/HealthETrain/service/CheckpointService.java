package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.POJP.Checkpoint;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author flora
* @description 针对表【checkpoint】的数据库操作Service
* @createDate 2024-07-07 11:52:45
*/
public interface CheckpointService extends IService<Checkpoint> {

    Checkpoint getCheckpointBySectionId(Long sectionId, Long userId);
}
