package com.conv.HealthETrain.domain.DTO;

import com.conv.HealthETrain.domain.Lesson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonInfoDTO {
    private Long lessonId;
    private String lessonName;
    private Integer lessonType;
    private Date startTime;
    private Date endTime;
    private String lessonCover;
    private List<String> teachers;

    public LessonInfoDTO(Lesson lesson, List<String> teachers) {
        this.lessonId = lesson.getLessonId();
        this.lessonName = lesson.getLessonName();
        this.lessonType = lesson.getLessonType();
        this.startTime = lesson.getStartTime();
        this.endTime = lesson.getEndTime();
        this.lessonCover = lesson.getLessonCover();
        this.teachers = teachers;
    }
}
