package com.conv.HealthETrain.domain.DTO;

import lombok.Data;
import java.util.Map;
@Data
public class LessonStatistic {
    // 本类用于统计选修和必修的课程总数以及——选修必修两类中七大类的数量
    private int compulsory;
    private int elective;
    private Map<String,Integer> compulsoryType;
    private Map<String,Integer> electiveType;
}
