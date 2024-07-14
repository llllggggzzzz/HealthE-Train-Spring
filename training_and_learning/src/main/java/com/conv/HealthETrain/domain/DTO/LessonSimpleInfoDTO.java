package com.conv.HealthETrain.domain.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonSimpleInfoDTO {
    // 简单的信息，避免联表查询
    @JsonProperty("key")
    private Long LessonId;
    @JsonProperty("lesson_name")
    private String LessonName;
    @JsonProperty("lesson_cover")
    private String LessonCover;
}
