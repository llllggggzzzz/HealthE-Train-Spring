package com.conv.HealthETrain.controller;

import com.conv.HealthETrain.mq.RabbitMQSender;
import com.conv.HealthETrain.utils.ByteUtil;
import com.conv.HealthETrain.utils.UniqueIdGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/*
afterConnectionEstablished ：连接成功后调用。
handleMessage ：处理发送来的消息。
handleTransportError： 连接出错时调用。
afterConnectionClosed ：连接关闭后调用。
supportsPartialMessages ：是否支持分片消息。（
传输大文件才用得到，一般直接使用return false就可以了）
 */
@Slf4j
@Component
@AllArgsConstructor
public class VideoSocketHandler extends TextWebSocketHandler {
    //所有连接的集合
    private static final Map<String, WebSocketSession> SESSIONS = new ConcurrentHashMap<>();

    private static final int videoChunkSize = 1024 * 1024 * 15;

    private final RabbitMQSender rabbitMQSender;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String key = session.getAttributes().get("key").toString();
        String path = session.getAttributes().get("path").toString();
        SESSIONS.put(key, session);
        log.info("成功建立连接-UUID: {}", key);
        // 开始发送消息
        log.info("开始发送数据");
        ByteUtil.readBytes(path, videoChunkSize, rabbitMQSender, key);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String msg = message.getPayload().toString();
        String key = session.getAttributes().get("key").toString();
        sendMessage(key,"服务器收到消息收到"+key+"发送的消息，消息内容为："+msg);
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
        String uuid = session.getAttributes().get("key").toString();
        log.info("{}的连接已关闭,status:{}", uuid, closeStatus);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 指定发消息
     * @param message
     */
    public static void sendMessage(String userId, String message) {
        WebSocketSession webSocketSession = SESSIONS.get(userId);
        log.info("发送消息: {}" , message);
        if (webSocketSession == null || !webSocketSession.isOpen()) return;
        try {
            webSocketSession.sendMessage(new TextMessage(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 群发消息
     * @param message 消息内容
     */
    public static void fanoutMessage(String message) {
        SESSIONS.keySet().forEach(us -> sendMessage(us, message));
    }

    /**
     * @description 发送字节流给客户端
     * @param uuid 表示唯一用户
     * @param data
     */
    public static void sendBinaryMessage(String uuid, byte[] data) {
        WebSocketSession webSocketSession = SESSIONS.get(uuid);
        if (webSocketSession == null || !webSocketSession.isOpen()) return;
        try {
            webSocketSession.sendMessage(new BinaryMessage(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
