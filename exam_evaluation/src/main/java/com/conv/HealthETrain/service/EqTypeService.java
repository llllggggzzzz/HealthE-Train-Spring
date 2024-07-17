package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.EqType;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author flora
* @description 针对表【eq_type】的数据库操作Service
* @createDate 2024-07-07 11:47:43
*/
public interface EqTypeService extends IService<EqType> {
    EqType getEqTypeIdByEqTypeName(String type);
}
