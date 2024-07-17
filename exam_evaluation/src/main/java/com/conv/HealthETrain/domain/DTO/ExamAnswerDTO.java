package com.conv.HealthETrain.domain.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExamAnswerDTO {
    private Long examId;
    private Long userId;
    private Long eqId;
    private Long eqTypeId;
    private String userAnswer;
    private String realAnswer;
    private Double getScore;
    private Integer sumScore;
}
