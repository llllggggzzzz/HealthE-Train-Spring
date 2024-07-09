package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.Checkpoint;
import com.conv.HealthETrain.service.CheckpointService;
import com.conv.HealthETrain.mapper.CheckpointMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
* @author flora
* @description 针对表【checkpoint】的数据库操作Service实现
* @createDate 2024-07-07 11:52:45
*/
@Service
@AllArgsConstructor
public class CheckpointServiceImpl extends ServiceImpl<CheckpointMapper, Checkpoint>
    implements CheckpointService{

    private final CheckpointMapper checkpointMapper;

    @Override
    public Checkpoint getCheckpointBySectionId(Long sectionId, Long userId) {
        LambdaQueryWrapper<Checkpoint> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Checkpoint::getSectionId, sectionId).eq(Checkpoint::getUserId, userId);
        return checkpointMapper.selectOne(lambdaQueryWrapper);
    }
}




