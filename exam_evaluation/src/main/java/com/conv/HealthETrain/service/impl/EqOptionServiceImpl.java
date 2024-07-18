package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.EqOption;
import com.conv.HealthETrain.service.EqOptionService;
import com.conv.HealthETrain.mapper.EqOptionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
* @author flora
* @description 针对表【eq_option】的数据库操作Service实现
* @createDate 2024-07-07 11:47:43
*/
@Service
@RequiredArgsConstructor
public class EqOptionServiceImpl extends ServiceImpl<EqOptionMapper, EqOption>
    implements EqOptionService{

    private final EqOptionMapper eqOptionMapper;

    @Override
    public EqOption getByEqId(Long eqId) {
        return lambdaQuery().eq(EqOption::getEqId, eqId).one();
    }

    @Override
    public EqOption insertEqOption(EqOption eqOption) {
        eqOptionMapper.insert(eqOption);
        return eqOption;
    }

    @Override
    public EqOption updateOptionByEqId(EqOption eqOption) {
        LambdaQueryWrapper<EqOption> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EqOption::getEqId, eqOption.getEqId());
        eqOptionMapper.update(eqOption, queryWrapper);

        return eqOption;
    }

    @Override
    public boolean deleteOptionByEqId(Long eqId) {
        LambdaQueryWrapper<EqOption> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EqOption::getEqId, eqId);
        return eqOptionMapper.delete(queryWrapper) > 0;
    }
}




