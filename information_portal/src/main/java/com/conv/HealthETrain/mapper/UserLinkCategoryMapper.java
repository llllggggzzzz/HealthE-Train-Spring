package com.conv.HealthETrain.mapper;

import com.conv.HealthETrain.domain.UserLinkCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author john
* @description 针对表【user_link_category】的数据库操作Mapper
* @createDate 2024-07-05 17:56:52
* @Entity com.conv.HealthETrain.domain.UserLinkCategory
*/
public interface UserLinkCategoryMapper extends BaseMapper<UserLinkCategory> {
    // 统计1-7的学生总数
    @Select("SELECT COUNT(*) FROM user_link_category WHERE category_id BETWEEN 1 AND 7")
    int countStudents();

    // 查询对应的信息
    @Select("SELECT * FROM user_link_category WHERE user_id = #{userId}")
    List<UserLinkCategory> selectLinkCategoriesByUserId(@Param("userId") Long userId);

    // 删除对应用户的出卷人身份
    @Delete("DELETE FROM user_link_category WHERE user_id = #{userId} AND category_id = #{categoryId}")
    void deleteCategoryForUser(@Param("userId") Long userId,@Param("categoryId") int categoryId);
}




