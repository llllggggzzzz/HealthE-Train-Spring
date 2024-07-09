package com.conv.HealthETrain.domain.VO;


import com.conv.HealthETrain.domain.Checkpoint;
import com.conv.HealthETrain.domain.Section;
import lombok.Data;

@Data
public class SectionCheckVO {
    private Section section;
    private Checkpoint checkpoint;
}
