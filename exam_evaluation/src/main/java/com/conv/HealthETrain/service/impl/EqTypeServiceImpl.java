package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.EqType;
import com.conv.HealthETrain.service.EqTypeService;
import com.conv.HealthETrain.mapper.EqTypeMapper;
import org.springframework.stereotype.Service;

/**
* @author flora
* @description 针对表【eq_type】的数据库操作Service实现
* @createDate 2024-07-07 11:47:43
*/
@Service
public class EqTypeServiceImpl extends ServiceImpl<EqTypeMapper, EqType>
    implements EqTypeService{

    @Override
    public EqType getEqTypeIdByEqTypeName(String type) {
        return lambdaQuery().eq(EqType::getEqTypeName, type).one();
    }
}




