package com.conv.HealthETrain.domain.dto;

import lombok.Data;

/**
 * @author liusg
 */
@Data
public class TeacherDetailDTO {
    private Long teacherId;
    private Long userId;
    private String category;
    private String realName;
    private String position;
    private String qualification;
}
