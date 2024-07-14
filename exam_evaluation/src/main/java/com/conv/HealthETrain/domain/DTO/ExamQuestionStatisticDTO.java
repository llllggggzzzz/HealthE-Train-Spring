package com.conv.HealthETrain.domain.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ExamQuestionStatisticDTO {
    // 仅展示部分数据，给后台看，不需要传答案
    @JsonProperty("key")
    private Long eqId;
    @JsonProperty("eq_type_id")
    private Long eqTypeId;
    @JsonProperty("node_content")
    private String noteContent;
}
