package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.Admin;
import com.conv.HealthETrain.service.AdminService;
import com.conv.HealthETrain.mapper.AdminMapper;
import org.springframework.stereotype.Service;

/**
* @author john
* @description 针对表【admin】的数据库操作Service实现
* @createDate 2024-07-05 17:56:52
*/
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin>
    implements AdminService{

}




