package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.Checkpoint;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author flora
* @description 针对表【checkpoint】的数据库操作Service
* @createDate 2024-07-07 11:52:45
*/
public interface CheckpointService extends IService<Checkpoint> {
    Checkpoint getCheckpointBySectionId(Long sectionId, Long userId);

    // 根据章节id查找章节的所学总人数
    int getCountUserByChapterId(Long chapterId);

    // 根据userId查询该名同学的必修课全部课程学习了多少;
    int countLearnedSectionsByUserId(Long userId);
}
