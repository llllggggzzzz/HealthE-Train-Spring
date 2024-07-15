package com.conv.HealthETrain.domain.DTO;

import com.conv.HealthETrain.domain.Answer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerDTO {
    private Answer answer;
    private NoteInfoDTO noteInfoDTO;
}
