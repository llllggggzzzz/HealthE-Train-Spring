package com.conv.HealthETrain.controller;

import com.conv.HealthETrain.domain.DTO.MessageResponseDTO;
import com.conv.HealthETrain.domain.DTO.RecentNoteDTO;
import com.conv.HealthETrain.domain.DTO.ShareNoteDTO;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.utils.UniqueIdGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@AllArgsConstructor
@RequestMapping("/message")
@Slf4j
public class MessageController {

    @PostMapping("/postShareMessage/{receiverId}")
    public ApiResponse<MessageResponseDTO> postShareMessage(@PathVariable Long receiverId,
                                                            @RequestBody ShareNoteDTO shareNoteDTO) {
        String uuid = UniqueIdGenerator.generateUniqueId(receiverId.toString(),
                new Date().toString());
        MessageResponseDTO messageResponseDTO = new MessageResponseDTO(uuid);
        log.info("生成uuid:" + uuid);
        return ApiResponse.success(messageResponseDTO);
    }
}
