package com.conv.HealthETrain.domain.DTO;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AddPaperDTO {
    private String paperTitle;
    private Integer sumScore;
    private Long creatorId;
    private Long lessonId;
    private Integer duration;
    private Integer level;
    private Double passScore;
    private Integer retryTimes;
    private Date startTime;
    private Date endTime;
    private String examName;
    private List<Long> questions;
    private List<Integer> scores;
}
