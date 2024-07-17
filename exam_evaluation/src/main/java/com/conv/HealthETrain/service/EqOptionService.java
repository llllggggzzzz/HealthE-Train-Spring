package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.EqOption;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author flora
* @description 针对表【eq_option】的数据库操作Service
* @createDate 2024-07-07 11:47:43
*/
public interface EqOptionService extends IService<EqOption> {
    EqOption getByEqId(Long eqId);

    EqOption insertEqOption(EqOption eqOption);

    EqOption updateOptionByEqId(EqOption eqOption);

    boolean deleteOptionByEqId(Long eqId);
}
