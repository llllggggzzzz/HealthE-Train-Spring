package com.conv.HealthETrain.mq;


import cn.hutool.core.util.StrUtil;
import com.conv.HealthETrain.controller.VideoSocketHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
@Slf4j
public class MessageHandler {
    private static Integer count = 0;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static String oldUUID = "";
    @RabbitListener(bindings = @QueueBinding(value = @Queue("videoDataQueue"),
            exchange = @Exchange("videoDataExchange"),
    key = {"videoDataRoutingKey"})
    )
    @RabbitHandler
    public void sendByteSteam(Map<String, Object> dataMap) {
        String uuid = (String)dataMap.get("uuid");
        byte[] data = objectMapper.convertValue(StrUtil.sub((String)(dataMap.get("data")), 1, -1), new TypeReference<byte[]>() {
        });
//        if(!oldUUID.equals(uuid)) {
//            count = 0;
//        }
//        oldUUID = uuid;
//        count += data.length;
//        log.info("消息发送: {}", count);
        VideoSocketHandler.sendBinaryMessage(uuid, data);
    }
}
