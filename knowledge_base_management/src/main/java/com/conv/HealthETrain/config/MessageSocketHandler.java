package com.conv.HealthETrain.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.conv.HealthETrain.domain.DTO.ChatMessage;
import com.conv.HealthETrain.enums.MessageType;
import com.conv.HealthETrain.mq.RabbitMQSender;
import com.conv.HealthETrain.utils.ByteUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@AllArgsConstructor
public class MessageSocketHandler extends TextWebSocketHandler {
    private static final Map<String, List<WebSocketSession>> USER_SESSIONS = new ConcurrentHashMap<>();
    private static final Map<String, Integer> USER_CONNECT_NUM_MAP = new ConcurrentHashMap<>();

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final StringRedisTemplate redisTemplate;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //接收当前请求连接用户的id
        String userId = session.getAttributes().get("senderId").toString();
        if(USER_SESSIONS.get(userId) != null){
            List<WebSocketSession> socketSessions = USER_SESSIONS.get(userId);
            socketSessions.add(session);
            USER_SESSIONS.put(userId, socketSessions);
        }else{
            List<WebSocketSession> socketSessions = new ArrayList<>();
            socketSessions.add(session);
            USER_SESSIONS.put(userId, socketSessions);
        }
        if (USER_CONNECT_NUM_MAP.get(userId) != null) {
            // userId 已存在，将连接数加 1
            int connectNum = USER_CONNECT_NUM_MAP.get(userId);
            USER_CONNECT_NUM_MAP.put(userId, connectNum + 1);
        } else {
            // userId 不存在，设置连接数为 1
            USER_CONNECT_NUM_MAP.put(userId, 1);
        }
        log.info("成功建立连接-userId:" + userId + "连接数为" + USER_CONNECT_NUM_MAP.get(userId));
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String payload = message.getPayload().toString();
        ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);
        MessageType messageType = chatMessage.getType();
        List<String> receiverIdList = chatMessage.getReceiverIdList();
        String senderId = chatMessage.getSenderUserId();
        String actualMessage = chatMessage.getMessage();
        //要转发的消息
//        TextMessage textMessage = new TextMessage(objectMapper.writeValueAsString(chatMessage));
        switch (messageType) {
            case BROADCAST_MSG:
            case PRIVATE_MSG:
            case NOTE_SHARE:
                // 群发/私发消息给所有活跃的会话
                synchronized (USER_SESSIONS) {
                    for (String userId : receiverIdList) {
                        List<WebSocketSession> webSocketSessionList = USER_SESSIONS.get(userId);
                        if(webSocketSessionList == null){
                            //为空,当前用户不在线，存储到未读聊天记录
                            String unreadKey = "unread:" + userId + ":From:" + senderId;
                            redisTemplate.opsForList().leftPush(unreadKey, JSONUtil.toJsonStr(chatMessage));
                            //存储聊天记录
                            String chatKey = "chat:" + userId + ":with:" + senderId;
                            redisTemplate.opsForList().leftPush(chatKey, JSONUtil.toJsonStr(chatMessage));
                            String reverseChatKey = "chat:" + senderId + ":with:" + userId;
                            redisTemplate.opsForList().leftPush(reverseChatKey, JSONUtil.toJsonStr(chatMessage));
                            return;
                        }
                        for (WebSocketSession socketSession : webSocketSessionList){
                            if (socketSession.isOpen() && socketSession != null) {
                                socketSession.sendMessage(message);
                            }
                        }
                        //redis存储聊天记录
                        String chatKey = "chat:" + userId + ":with:" + senderId;
                        redisTemplate.opsForList().leftPush(chatKey, JSONUtil.toJsonStr(chatMessage));
                        String reverseChatKey = "chat:" + senderId + ":with:" + userId;
                        redisTemplate.opsForList().leftPush(reverseChatKey, JSONUtil.toJsonStr(chatMessage));
                    }
                }
                break;
            default:
                // 处理未知类型的消息
                System.out.println("未知类型消息: " + messageType);
                break;
        }
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        super.handleBinaryMessage(session, message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("连接出错");
        if (session.isOpen()) {
            session.close();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        String userId = session.getAttributes().get("senderId").toString();
        List<WebSocketSession> webSocketSessionList = USER_SESSIONS.get(userId);
        webSocketSessionList.remove(session);
        if(webSocketSessionList.isEmpty()){
            USER_SESSIONS.remove(userId);
            USER_CONNECT_NUM_MAP.remove(userId);
            log.info("{}的连接已关闭,status:{}", userId, closeStatus);
        }else{
            int connectNum = USER_CONNECT_NUM_MAP.get(userId);
            USER_CONNECT_NUM_MAP.put(userId, connectNum - 1);
            log.info("{}的连接减一,status:{}", userId, closeStatus);
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

}
