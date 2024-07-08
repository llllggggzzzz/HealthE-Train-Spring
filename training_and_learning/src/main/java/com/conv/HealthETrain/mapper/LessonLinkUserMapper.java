package com.conv.HealthETrain.mapper;

import com.conv.HealthETrain.domain.POJP.LessonLinkUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.conv.HealthETrain.domain.VO.LessonDetailVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author flora
* @description 针对表【lesson_link_user】的数据库操作Mapper
* @createDate 2024-07-07 11:52:45
* @Entity com.conv.HealthETrain.domain.POJP.LessonLinkUser
*/
public interface LessonLinkUserMapper extends BaseMapper<LessonLinkUser> {
    @Select("SELECT lesson_id FROM lesson_link_user WHERE user_id = #{userId}")
    List<Long> getLessonIdsByUserId(@Param("userId") Long userId);
}




