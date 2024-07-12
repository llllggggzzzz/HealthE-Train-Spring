package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.conv.HealthETrain.domain.dto.UserDetailDTO;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

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

     // 查询用户基本情况以及教师类别和权限类别
     List<UserDetailDTO> getAllUsersWithDetails();

     // 查询所有用户
     List<User> findStudentUserList();

     // 封装加密方法，把加密后的密码返回
     String encryption(String password);

}
