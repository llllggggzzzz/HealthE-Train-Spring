package com.conv.HealthETrain.domain.DTO;

import com.conv.HealthETrain.domain.VO.ChapterStatusVO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class StudentProcessDTO {
    // 学生个人在某一个课程内，每一个chapter对应的学习状态
    @JsonProperty("key")
    private Long userId;
    private String cover;
    private String username;
    private String account;
    @JsonProperty("chapter_status")
    List<ChapterStatusVO> chapterStatus;
}
