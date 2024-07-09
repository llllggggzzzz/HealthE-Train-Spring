package com.conv.HealthETrain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.conv.HealthETrain.domain.TeacherDetail;
import com.conv.HealthETrain.service.TeacherDetailService;
import com.conv.HealthETrain.mapper.TeacherDetailMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author john
* @description 针对表【teacher_detail】的数据库操作Service实现
* @createDate 2024-07-05 17:56:52
*/
@Service
@RequiredArgsConstructor
public class TeacherDetailServiceImpl extends ServiceImpl<TeacherDetailMapper, TeacherDetail>
    implements TeacherDetailService{

    private final TeacherDetailMapper teacherDetailMapper;

    // 按照学历分类统计教师数量
    @Override
    public  Map<String, Integer>  countTeachersByQualification() {
        List<TeacherDetail> teacherDetails = teacherDetailMapper.selectList(null);  // 获取所有教师详情列表
        Map<String, Integer> qualificationCounts = new HashMap<>();
        for (TeacherDetail teacher : teacherDetails) {
            Integer count = qualificationCounts.getOrDefault(teacher.getQualificationId().toString(), 0);
            qualificationCounts.put(teacher.getQualificationId().toString(), count + 1);
        }
        return qualificationCounts;
    }

    // 统计教师数量
    @Override
    public int countTeachers() {
        return teacherDetailMapper.countTeachers();
    }
}




