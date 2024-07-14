package com.conv.HealthETrain.domain.DTO;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class LessonCategoryInfoDTO {
    // 在原有Lesson基础上添加一个categoryList
    @JsonProperty("key")
    private Long lessonId;
    private String lessonName;
    private Integer lessonType;
    private Date startTime;
    private Date endTime;
    private String lessonCover;
    private List<Long> categoryList;
}
