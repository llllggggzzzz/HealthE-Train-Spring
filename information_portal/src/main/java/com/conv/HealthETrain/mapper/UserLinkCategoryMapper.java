package com.conv.HealthETrain.mapper;

import com.conv.HealthETrain.domain.UserLinkCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
* @author john
* @description 针对表【user_link_category】的数据库操作Mapper
* @createDate 2024-07-05 17:56:52
* @Entity com.conv.HealthETrain.domain.UserLinkCategory
*/
public interface UserLinkCategoryMapper extends BaseMapper<UserLinkCategory> {
    @Select("SELECT COUNT(*) FROM user_link_category WHERE category_id BETWEEN 1 AND 7")
    int countStudents();
}




