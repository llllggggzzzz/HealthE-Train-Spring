package com.conv.HealthETrain.domain.DTO;

import com.conv.HealthETrain.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    private String senderUserId;
    private List<String> receiverIdList;
    private String message;
    private NoteInfoDTO chatNote;
    private MessageType type;
}
