package com.conv.HealthETrain.domain.VO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChapterStatusVO {
    // 每个chapter对应的状态，1为学习完毕，0为学习中，-1为未学习
    @JsonProperty("chapter_id")
    private Long chapterId;
    private int status;
}
