package com.conv.HealthETrain.controller;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.conv.HealthETrain.domain.domain.Message;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Component
@AllArgsConstructor
public class LiveSocketHandler extends TextWebSocketHandler {
    //所有连接的集合
    //<userId, <streamId, session>>
    private static final Map<String, List<Map<String, WebSocketSession>>> SESSIONS = new ConcurrentHashMap<>();
    //<streamId, session>
    private static final Map<String, List<WebSocketSession>> GROUPS = new ConcurrentHashMap<>();
    //记录同一用户的连接数,当连接数为零的时候关闭连接,删除通信
    private static final Map<Map<String, String>, Integer> TIMES = new ConcurrentHashMap<>();
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = session.getAttributes().get("userId").toString();
        String streamId = session.getAttributes().get("streamId").toString();
        if("-1".equals(userId)) {
            return;
        }
        // 先检查连接是否存在,如果存在则不进行建立,只添加times
        List<Map<String, WebSocketSession>> maps = SESSIONS.get(userId);
        if(maps != null ) {
            for (Map<String, WebSocketSession> map : maps) {
                if(map.containsKey(streamId)) {
                    // 已经包含, 不重复添加到GROUP和SESSIONS
                    updateTimes(userId, streamId);
                    log.info("添加计数+1: userId-{}, streamId-{}, times-{}", userId, streamId, getThridValue(userId, streamId));

                    return;
                }
            }
        }
        // 连接不存在, 添加新的映射到SESSION
        Map<String, WebSocketSession> innerMap = new HashMap<>();
        innerMap.put(streamId, session);
        if(SESSIONS.containsKey(userId)) {
            List<Map<String, WebSocketSession>> sessionMaps = SESSIONS.get(userId);
            sessionMaps.add(innerMap);
        } else {
            // 不包含此key, 建立新的List
            ArrayList<Map<String, WebSocketSession>> list = new ArrayList<>();
            list.add(innerMap);
            SESSIONS.put(userId, list);
        }

        // 添加新的GROUP映射
        if(GROUPS.containsKey(streamId)) {
            List<WebSocketSession> webSocketSessions = GROUPS.get(streamId);
            webSocketSessions.add(session);
        } else {
            ArrayList<WebSocketSession> list = new ArrayList<>();
            list.add(session);
            GROUPS.put(streamId, list);
        }

        // TIMES + 1
        updateTimes(userId, streamId);

        log.info("加入新的连接: userId-{} streamId-{}", userId, streamId);
        log.info("分组出现新的成员: group-{} user-{}", streamId, userId);
    }


    private void updateTimes(String userId, String streamId) {
        boolean found = false;
        for (Map.Entry<Map<String, String>, Integer> entry : TIMES.entrySet()) {
            Map<String, String> keyMap = entry.getKey();
            if (keyMap.containsKey(userId) &&
                    keyMap.get(userId).equals(streamId) ) {
                TIMES.put(keyMap, entry.getValue() + 1);
                found = true;
                break;
            }
        }
        if (!found) {
            Map<String, String> newKeyMap = new HashMap<>();
            newKeyMap.put(userId, streamId);
            TIMES.put(newKeyMap, 1);
        }
    }


    /**
     * @description 分发消息
     * @param session 会话
     * @param message 消息
     * @throws Exception
     */
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        log.info("收到消息: {}", message.getPayload());
        Message receivedMessage = parseToMessage(message.getPayload().toString());
        log.info("message: {}", receivedMessage);
        String streamId = receivedMessage.getStreamId();
        String messageContent = receivedMessage.getMessage();
        log.info("获取到streamId");
        List<WebSocketSession> webSocketSessions = GROUPS.get(streamId);
        if(webSocketSessions == null || webSocketSessions.isEmpty()) {
            return;
        }
        for (WebSocketSession webSocketSession : webSocketSessions) {
            if(webSocketSession != null && webSocketSession.isOpen()) {
                log.info("接收到消息 GROUP: {}, messageContent: {}", streamId, messageContent);
                webSocketSession.sendMessage(message);
            }
        }
    }

    public static void sendMessage(String streamId, String message) throws IOException {
        if(GROUPS.containsKey(streamId)) {
            List<WebSocketSession> webSocketSessions = GROUPS.get(streamId);
            if(webSocketSessions != null) {
                log.info("remove groups number: {}", webSocketSessions.size());
                for (WebSocketSession webSocketSession : webSocketSessions) {
                    if(webSocketSession != null && webSocketSession.isOpen()) {
                        webSocketSession.sendMessage(new TextMessage(message));
                    }
                }
            }
        }
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        super.handleBinaryMessage(session, message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("连接出错 SESSION: {}, EXCEPTION: {}", session.toString(), exception.toString());
        if (session.isOpen()) {
            session.close();
        }
    }

    /**
     * @description 抛出异常
     * @param session 连接会话
     * @param closeStatus 关闭状态
     * @throws Exception 异常状态
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        String userId = session.getAttributes().get("userId").toString();
        String streamId = session.getAttributes().get("streamId").toString();
        if("-1".equals(userId)) {
            return;
        }
        log.info("userId:{}-streamId:{} 的连接已关闭,status:{}", userId, streamId, closeStatus);
        // 次数减一, 如果记录为零,则从GROUPS中移除
        Integer time = getThridValue(userId, streamId);
        Map<String, String> key = getThridKey(userId, streamId);
        log.info("userId: {}, streamId: {}, times:{}", userId, streamId, time);
        if(time > 1) {
            TIMES.put(key, time-1);
        } else {
            // 移除SESSON, 移除TIME
            log.info("要移除的key: {}", key);
            TIMES.remove(key);
            // 删除此流下的用户session
            List<Map<String, WebSocketSession>> sessionMap = SESSIONS.get(userId);
            if(sessionMap == null || sessionMap.isEmpty()) {
                return;
            }
            sessionMap.removeIf(stringWebSocketSessionMap -> stringWebSocketSessionMap.containsKey(streamId));
        }
    }

    private Integer getThridValue(String userId, String streamId) {
        if( TIMES.isEmpty()) {
            return -1;
        }
        for (Map.Entry<Map<String, String>, Integer> entry : TIMES.entrySet()) {
            Map<String, String> keyMap = entry.getKey();
            if (keyMap.containsKey(userId) && keyMap.get(userId).equals(streamId)) {
                return entry.getValue();
            }
        }
        return -1;
    }

    private Map<String, String> getThridKey(String userId, String streamId) {
        if(TIMES.isEmpty()) {
            return null;
        }
        for (Map.Entry<Map<String, String>, Integer> entry : TIMES.entrySet()) {
            Map<String, String> keyMap = entry.getKey();
            if (keyMap.containsKey(userId) && keyMap.get(userId).equals(streamId)) {
                return keyMap;
            }
        }
        return null;
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }


    private static Message parseToMessage(String jsonString) {
        // 使用Hutool JSONUtil解析JSON字符串
        JSONObject jsonObject = JSONUtil.   parseObj(jsonString);

        // 提取header中的值
        JSONObject header = jsonObject.getJSONObject("header");
        String from = header.containsKey("from") ? header.getStr("from") : null;
        String steramId = header.containsKey("streamId") ? header.getStr("streamId") : null;
        String userName = header.containsKey("userName") ? header.getStr("userName") : null;

        // 提取body中的值
        JSONObject body = jsonObject.getJSONObject("body");
        String message = body.containsKey("message") ? body.getStr("message") : null;
        String date = body.containsKey("date") ? body.getStr("date") : null;

        // 提取type的值
        Integer type = jsonObject.containsKey("type") ? jsonObject.getInt("type") : null;

        return new Message(from, steramId, userName, message, date, type);
    }
}
