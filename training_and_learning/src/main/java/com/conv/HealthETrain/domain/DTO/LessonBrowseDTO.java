package com.conv.HealthETrain.domain.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class LessonBrowseDTO {
    // 用于课程浏览界面的DTO
    @JsonProperty("lesson_id")
    private Long lessonId;
    @JsonProperty("lesson_name")
    private String lessonName;
    @JsonProperty("start_time")
    private Date startTime;
    @JsonProperty("end_time")
    private Date endTime;
    @JsonProperty("lesson_cover")
    private String lessonCover;
    @JsonProperty("categories")
    private List<Long> categoryList;
    private double star;
    private String objects;
}
