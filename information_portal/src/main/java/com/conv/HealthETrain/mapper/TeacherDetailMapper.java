package com.conv.HealthETrain.mapper;

import com.conv.HealthETrain.domain.TeacherDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
* @author john
* @description 针对表【teacher_detail】的数据库操作Mapper
* @createDate 2024-07-05 17:56:52
* @Entity com.conv.HealthETrain.domain.TeacherDetail
*/
public interface TeacherDetailMapper extends BaseMapper<TeacherDetail> {
    @Select("SELECT COUNT(*) FROM teacher_detail WHERE qualification_id BETWEEN 1 AND 4")
    int countTeachers();
}




