package com.conv.HealthETrain.domain.DTO;

import lombok.Data;
import com.conv.HealthETrain.domain.DTO.ll.ExamQuestionDTO;
import java.util.Date;
import java.util.List;

/**
 * @author liusg
 */
@Data
public class QuestionBankDTO {
    private Long qbId;
    private String qbTitle;
    private Date createTime;
    private Long lessonId;
    private String lessonName;
    private Integer lessonType;
    private Date startTime;
    private Date endTime;
    private String lessonCover;
    private List<ExamQuestionDTO> examQuestions;
}
