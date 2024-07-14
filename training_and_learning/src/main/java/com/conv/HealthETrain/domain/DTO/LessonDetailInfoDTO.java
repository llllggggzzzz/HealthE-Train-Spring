package com.conv.HealthETrain.domain.DTO;

import lombok.Data;

import java.util.Date;

/**
 * @author liusg
 */
@Data
public class LessonDetailInfoDTO {
    private Long lessonId;
    private String lessonName;
    private Integer lessonType;
    private Date startTime;
    private Date endTime;
    private String lessonCover;
    private String lessonOverview;
    private String lessonObject;
    private String preliminaryKnowledge;
    private String referenceMaterial;
}
