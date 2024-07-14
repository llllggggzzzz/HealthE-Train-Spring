package com.conv.HealthETrain.domain.DTO;
import com.conv.HealthETrain.domain.Checkpoint;
import com.conv.HealthETrain.domain.Section;
import com.conv.HealthETrain.domain.domain.Video;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoLoadDTO {
    private Video video;
    private String uuid;
    private Section section;
    private Checkpoint checkpoint;
}
