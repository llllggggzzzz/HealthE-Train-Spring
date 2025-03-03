package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.Privilege;
import com.conv.HealthETrain.service.PrivilegeService;
import com.conv.HealthETrain.mapper.PrivilegeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author flora
* @description 针对表【privilege】的数据库操作Service实现
* @createDate 2024-07-08 14:30:51
*/
@Service
public class PrivilegeServiceImpl extends ServiceImpl<PrivilegeMapper, Privilege>
    implements PrivilegeService{

    @Autowired
    private PrivilegeMapper privilegeMapper;
    @Override
    public Privilege findPrivilegeByPrivilegeId(Long id) {
        QueryWrapper<Privilege> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("privilege_id", id);
        return privilegeMapper.selectOne(queryWrapper);
    }
}




