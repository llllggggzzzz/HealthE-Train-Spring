package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.Admin;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author john
* @description 针对表【admin】的数据库操作Service
* @createDate 2024-07-05 17:56:52
*/
public interface AdminService extends IService<Admin> {
    String loginByAccountForAdmin(Admin loginAdmin);
    public boolean registerAdmin(Admin admin);
}
