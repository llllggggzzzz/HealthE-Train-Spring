package com.conv.HealthETrain.domain.VO;

import java.util.Date;
import java.util.List;

public class LessonDetailVO {
    private Long lessonId;
    private String lessonName;
    private Integer lessonType;
    private Date startTime;
    private Date endTime;
    private String lessonCover;
    private List<Long> teacherIds;
}
