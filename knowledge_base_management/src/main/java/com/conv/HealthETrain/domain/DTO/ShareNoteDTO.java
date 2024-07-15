package com.conv.HealthETrain.domain.DTO;

import cn.hutool.extra.spring.SpringUtil;
import com.conv.HealthETrain.domain.Note;
import com.conv.HealthETrain.domain.NoteLinkRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShareNoteDTO {
    private Note note;
    private NoteLinkRepository noteLinkRepository;
    private String userName;
    private String cover;
    private String message;
}
