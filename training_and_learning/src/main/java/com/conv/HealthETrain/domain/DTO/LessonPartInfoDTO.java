package com.conv.HealthETrain.domain.DTO;

import lombok.Data;

import java.util.List;

/**
 * @author liusg
 */
@Data
public class LessonPartInfoDTO {
    private Long lessonId;
    private List<ChapterInfoDTO> chapters;
}
