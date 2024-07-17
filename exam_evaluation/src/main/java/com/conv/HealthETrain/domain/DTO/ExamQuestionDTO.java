package com.conv.HealthETrain.domain.DTO;


import com.conv.HealthETrain.domain.EqOption;
import lombok.Data;
import com.conv.HealthETrain.domain.Note;
import lombok.ToString;

@Data
@ToString
public class ExamQuestionDTO {
    private Long eqId;
    private Long examQuestionId;
    private Long eqTypeId;
    private Note note;
    private EqOption eqOption;
    private Integer score;
}
