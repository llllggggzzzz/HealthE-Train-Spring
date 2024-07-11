package com.conv.HealthETrain.domain.dto;


import lombok.Data;

import java.util.Map;

@Data
public class UserStatistic {
    // 学生总人数，教师总人数，学生每类的人数，教师每种学历的人数
    private int studentNumber;
    private int teacherNumber;
    private Map<String,Integer> studentType;
    private Map<String,Integer> teacherType;
}
