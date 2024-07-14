package com.conv.HealthETrain.domain.DTO;

import com.conv.HealthETrain.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    private String senderUserId;
    private UserDTO senderUserInfo;
    private List<String> receiverIdList;
    private String message;
    private RecentNoteDTO chatNote;
    private MessageType type;
    private Date sendTime;

}
