package com.conv.HealthETrain.domain.DTO;

import lombok.Data;

import java.util.List;

@Data
public class CreatPaperQuestionDTO {
    // 考试创建试卷时查阅题目需要的TDO
    private Long eqId;
    private String answer;
    private String content;
    private Long eqTypeId;
    private List<String> options;
}
