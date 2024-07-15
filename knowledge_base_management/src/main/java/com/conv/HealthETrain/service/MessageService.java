package com.conv.HealthETrain.service;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.conv.HealthETrain.domain.DTO.ChatMessage;
import com.conv.HealthETrain.enums.ResponseCode;
import com.conv.HealthETrain.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MessageService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;  // 使用Spring自动注入


    /**
    * @Description: 获取用户和指定用户的聊天记录
    * @Param:
    * @return:
    * @Author: flora
    * @Date: 2024/7/14
    */
    public List<ChatMessage> getMessageListWithChatId(Long userId, Long chatId){
        String chatKey = "chat:" + userId + ":with:" + chatId;
        List<String> chatMessage = redisTemplate.opsForList().range(chatKey, 0, -1);
        List<ChatMessage> chatMessageList = new ArrayList<>();
        if(chatMessage != null){
            try {
                for (String messageJson : chatMessage) {
                    ChatMessage message = JSONUtil.toBean(messageJson, ChatMessage.class);
//                    ChatMessage message = objectMapper.readValue(messageJson, ChatMessage.class);
                    chatMessageList.add(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return chatMessageList;
    }
    /**
    * @Description: 获取指定用户的全部聊天记录
    * @Param:
    * @return:
    * @Author: flora
    * @Date: 2024/7/14
    */
    public List<ChatMessage> getAllChatList(Long userId){
        Set<String> keyList = redisTemplate.keys("chat:" + userId + ":with:*");
        List<ChatMessage> allMessageList = new ArrayList<>();
        if(keyList != null){
            for(String key: keyList){
//                String chatId = key.substring(key.lastIndexOf(":") + 1);
                List<String> chatMessageList = redisTemplate.opsForList().range(key, 0, -1);
//                List<ChatMessage> allMessageList = new ArrayList<>();
                if(chatMessageList != null){
                    try {
                        for (String messageJson : chatMessageList) {
                            ChatMessage message = JSONUtil.toBean(messageJson, ChatMessage.class);
//                            ChatMessage message = objectMapper.readValue(messageJson, ChatMessage.class);
                            allMessageList.add(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return allMessageList;
    }
    /**
    * @Description: 获取用户与指定用户的的未读聊天记录
    * @Param:
    * @return:
    * @Author: flora
    * @Date: 2024/7/14
    */
    public List<ChatMessage> getUnReadMessageList(Long userId, Long senderId){
        String unreadKey = "unread:" + userId + ":From:" + senderId;
        List<String> unreadMessages = redisTemplate.opsForList().range(unreadKey, 0, -1);
        List<ChatMessage> allMessageList = new ArrayList<>();
        if(unreadMessages != null){
            try {
                for (String messageJson : unreadMessages) {
                    ChatMessage message = JSONUtil.toBean(messageJson, ChatMessage.class);
//                    ChatMessage message = objectMapper.readValue(messageJson, ChatMessage.class);
                    allMessageList.add(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        redisTemplate.delete(unreadKey); // 清空未读消息列表
        return allMessageList;
    }
    /**
    * @Description: 获取全部未读聊天记录
    * @Param:
    * @return:
    * @Author: flora
    * @Date: 2024/7/14
    */
    public List<ChatMessage> getAllUnReadMessageList(Long userId){
        List<ChatMessage> allMessageList = new ArrayList<>();
        Set<String> keyList = redisTemplate.keys("unread:" + userId + ":From:*");
        if(keyList != null){
            for(String key: keyList){
                String senderId = key.substring(key.lastIndexOf(":") + 1);
                List<String> chatMessageList = redisTemplate.opsForList().range(key, 0, -1);
                if(chatMessageList != null){
                    try {
                        for (String messageJson : chatMessageList) {
                            ChatMessage message = JSONUtil.toBean(messageJson, ChatMessage.class);
//                            ChatMessage message = objectMapper.readValue(messageJson, ChatMessage.class);
                            allMessageList.add(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return allMessageList;
    }
}
