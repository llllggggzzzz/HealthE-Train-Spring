package com.conv.HealthETrain.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonSimpleInfoDTO {
    // 简单的信息，避免联表查询
    private Long LessonId;
    private String LessonName;
    private String LessonCover;
}
