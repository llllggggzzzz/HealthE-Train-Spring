package com.conv.HealthETrain.domain.DTO;

import com.conv.HealthETrain.domain.Chapter;
import lombok.Data;

import java.util.List;

@Data
public class LessonStudentSituationDTO {
    // 学生列表和学习情况
    private List<StudentProcessDTO> studentProcessDTOList;
    // 章节列表
    private List<Chapter> chaptersList;
}
