package com.conv.HealthETrain.domain.DTO;

import com.conv.HealthETrain.domain.Exam;
import lombok.*;

import java.util.Date;


@Data
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ExamDTO {
    private long examId;
    private long paperId;
    private long creatorId;
    private long lessonId;
    private int duration;
    private int level;
    private Double passScore;
    private int retryTimes;
    private Date startTime;
    private Date endTime;
    private String examName;
    private String teacherName;
    private String teacherCover;

    public ExamDTO(Exam exam) {
        this.examId = exam.getExamId();
        this.paperId = exam.getPaperId();
        this.creatorId = exam.getCreatorId();
        this.lessonId = exam.getLessionId();
        this.duration = exam.getDuration();
        this.level = exam.getLevel();
        this.passScore = exam.getPassScore();
        this.retryTimes = exam.getRetryTimes();
        this.startTime = exam.getStartTime();
        this.endTime = exam.getEndTime();
        this.examName = exam.getExamName();
    }
}
