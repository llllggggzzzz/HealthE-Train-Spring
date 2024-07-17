package com.conv.HealthETrain.controller;

import com.conv.HealthETrain.domain.DTO.ChatMessage;
import com.conv.HealthETrain.domain.DTO.MessageResponseDTO;
import com.conv.HealthETrain.domain.DTO.RecentNoteDTO;
import com.conv.HealthETrain.domain.DTO.ShareNoteDTO;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.MessageService;
import com.conv.HealthETrain.utils.UniqueIdGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/message")
@Slf4j
public class MessageController {
    private final MessageService messageService;

    /**
    * @Description: 获取当前用户的全部消息列表
    * @Param:
    * @return:
    * @Author: flora
    * @Date: 2024/7/14
    */
    @GetMapping("/chatList/{userId}")
    public ApiResponse<List<ChatMessage>> getAllChatList(@PathVariable Long userId){
        List<ChatMessage> allChatList = messageService.getAllChatList(userId);
        if(allChatList != null){
            log.info("获取" + userId+ "全部消息列表成功！");
            return ApiResponse.success(allChatList);
        }else{
            log.info(userId+ "无消息");
            return ApiResponse.success();
        }
    }

    /**
    * @Description: 获取当前用户的全部未读消息列表
    * @Param:
    * @return:
    * @Author: flora
    * @Date: 2024/7/14
    */
    @GetMapping("/unreadList/{userId}")
    public ApiResponse<List<ChatMessage>> getAllUnreadChatList(@PathVariable Long userId){
        List<ChatMessage> allUnreadChatList = messageService.getAllUnReadMessageList(userId);
        if(allUnreadChatList != null){
            log.info("获取" + userId+ "全部未读消息列表成功！");
            return ApiResponse.success(allUnreadChatList);
        }else{
            log.info(userId+ "无未读消息");
            return ApiResponse.success();
        }
    }


//    @PostMapping("/postShareMessage/{receiverId}")
//    public ApiResponse<MessageResponseDTO> postShareMessage(@PathVariable Long receiverId,
//                                                            @RequestBody ShareNoteDTO shareNoteDTO) {
//        String uuid = UniqueIdGenerator.generateUniqueId(receiverId.toString(),
//                new Date().toString());
//        MessageResponseDTO messageResponseDTO = new MessageResponseDTO(uuid);
//        log.info("生成uuid:" + uuid);
//        return ApiResponse.success(messageResponseDTO);
//    }

}
