package com.conv.HealthETrain.mapper;

import com.conv.HealthETrain.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author john
* @description 针对表【user】的数据库操作Mapper
* @createDate 2024-07-05 17:56:52
* @Entity com.conv.HealthETrain.domain.User
*/
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT u.* " +
            "FROM user u " +
            "INNER JOIN user_link_category ulc ON u.user_id = ulc.user_id " +
            "WHERE ulc.category_id IN (1, 2, 3, 4, 5, 6, 7)")
    List<User> findStudentUserList();
}




