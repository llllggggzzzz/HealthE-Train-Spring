package com.conv.HealthETrain.domain.DTO.ll;

import lombok.Data;

/**
 * @author liusg
 * @author liusg
 */
@Data
public class ExamQuestionDTO {
    private Long eqId;
    private String eqType;
    private String noteContent;
    private String answer;
    private String eqA;
    private String eqB;
    private String eqC;
    private String eqD;
}
