package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author john
* @description 针对表【user】的数据库操作Service
* @createDate 2024-07-05 17:56:52
*/
public interface UserService extends IService<User> {
     String loginByAccount(User loginUser);

     String loginByPhone(User loginUser);

     void sendEmailCode(User loginUser);

     boolean verifyEmail(User loginUser, String code);

     boolean register(User registerUser);
}
