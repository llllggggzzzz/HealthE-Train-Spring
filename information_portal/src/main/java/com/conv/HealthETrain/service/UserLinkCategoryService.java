package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.UserLinkCategory;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
* @author john
* @description 针对表【user_link_category】的数据库操作Service
* @createDate 2024-07-05 17:56:52
*/
public interface UserLinkCategoryService extends IService<UserLinkCategory> {

    // 获得七类学生各自的数量
    Map<String,Integer> countStudentsByCategory();

    public int countStudents();
}
