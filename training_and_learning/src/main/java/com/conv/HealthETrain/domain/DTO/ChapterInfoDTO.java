package com.conv.HealthETrain.domain.DTO;

import lombok.Data;

import java.util.List;

/**
 * @author liusg
 */
@Data
public class ChapterInfoDTO {
    private Long chapterId;
    private String chapterTitle;
    private Integer chapterOrder;
    private List<SectionInfoDTO> sections;
}
