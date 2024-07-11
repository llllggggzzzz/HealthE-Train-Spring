package com.conv.HealthETrain.domain.DTO;

import com.conv.HealthETrain.domain.Chapter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChapterStatistic {
    // 章节以及所学人数
    private Chapter chapter;
    private int count;
}
