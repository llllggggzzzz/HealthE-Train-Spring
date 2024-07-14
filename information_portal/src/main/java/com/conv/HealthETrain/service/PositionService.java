package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.Position;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author john
* @description 针对表【position】的数据库操作Service
* @createDate 2024-07-05 17:56:52
*/
public interface PositionService extends IService<Position> {
    String getPositionById(Long positionId);
}
