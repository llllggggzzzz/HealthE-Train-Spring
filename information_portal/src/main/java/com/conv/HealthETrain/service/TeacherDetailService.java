package com.conv.HealthETrain.service;

import com.conv.HealthETrain.domain.TeacherDetail;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
* @author john
* @description 针对表【teacher_detail】的数据库操作Service
* @createDate 2024-07-05 17:56:52
*/
public interface TeacherDetailService extends IService<TeacherDetail> {
    // 按照学历分类统计教师数量
    Map<String, Integer> countTeachersByQualification();

    TeacherDetail getByUserId(Long userId);

    // 统计教师总数量
    int countTeachers();
}
