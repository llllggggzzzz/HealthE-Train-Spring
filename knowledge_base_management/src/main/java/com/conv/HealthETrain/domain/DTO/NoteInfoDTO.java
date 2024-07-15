package com.conv.HealthETrain.domain.DTO;

import com.conv.HealthETrain.domain.Note;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NoteInfoDTO {
    private Note note;
    private String userName;
    private String cover;

    public NoteInfoDTO(Note note, String userName, String cover){
        this.note = note;
        this.userName = userName;
        this.cover = cover;
    }
}
