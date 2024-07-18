package com.conv.HealthETrain.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author liusg
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonDetailInfoDTO {
    private Long lessonId;
    private String lessonName;
    private Integer lessonType;
    private Date startTime;
    private Date endTime;
    private String lessonCover;
    private List<Boolean> lessonCategories;
    private String lessonOverview;
    private String lessonObject;
    private String preliminaryKnowledge;
    private String referenceMaterial;
}
