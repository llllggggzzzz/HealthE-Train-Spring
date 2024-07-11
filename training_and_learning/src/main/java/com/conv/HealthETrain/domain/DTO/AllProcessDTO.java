package com.conv.HealthETrain.domain.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
@Data
public class AllProcessDTO {
    // 某名学生的必修课学习进度，计算方法是学习section总数/总section数
    @JsonProperty("key")
    private Long userId;
    private String cover;
    private String username;
    private String account;
    private Integer processLine;
    private Integer processRound;
}
