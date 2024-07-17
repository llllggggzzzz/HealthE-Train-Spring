package com.conv.HealthETrain.domain.DTO;

import lombok.Data;

import java.util.Date;

// 试卷对具体学生展示的信息
@Data
public class LessonExamInfoDTO {
    private Long examId;
    private Long paperId;
    private Integer duration;
    private Integer level;
    private Double passScore;
    private Integer retryTimes;
    private Integer remainTimes;
    private Date startTime;
    private Date endTime;
    private String examName;
    private Integer score;
    private Integer timeCost;
}
