package com.conv.HealthETrain.domain.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ApplyLiveUUDTO {
    private Long userId;
    private String userName;
    private String cover;
    private String realName;
    private Long categoryId;
    private Long positionId;
    private String liveTitle;
    private String livaCover;
    private String liveIntroduction;
    private String categoryName="";
    @JsonProperty("uuid")
    private String UUID;
}
