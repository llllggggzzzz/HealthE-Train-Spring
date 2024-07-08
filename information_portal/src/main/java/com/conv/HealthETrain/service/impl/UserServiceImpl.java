package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.User;
import com.conv.HealthETrain.service.UserService;
import com.conv.HealthETrain.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author john
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-07-05 17:56:52
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> getAllUsers() {
        List<User> userList = userMapper.selectList(null);
        return userList;
    }
}




