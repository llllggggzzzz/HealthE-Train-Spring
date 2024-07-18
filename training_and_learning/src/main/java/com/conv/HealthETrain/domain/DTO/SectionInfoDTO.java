package com.conv.HealthETrain.domain.DTO;

import com.conv.HealthETrain.domain.Courseware;
import lombok.Data;

/**
 * @author liusg
 */
@Data
public class SectionInfoDTO {
    private Long sectionId;
    private String sectionTitle;
    private Integer sectionOrder;
    private String videoPath;
    private Long videoSize;
    private Long videoLength;
    private Courseware courseware;
}
