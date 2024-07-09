package com.conv.HealthETrain.domain.DTO;

import com.conv.HealthETrain.domain.POJP.Chapter;
import com.conv.HealthETrain.domain.VO.SectionCheckVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChapterDTO {
    private Chapter chapter;
    private List<SectionCheckVO> sectionCheckVOS;
}
