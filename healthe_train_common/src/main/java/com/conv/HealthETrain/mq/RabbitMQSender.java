package com.conv.HealthETrain.mq;

import cn.hutool.core.map.MapUtil;
import com.conv.HealthETrain.callback.DataCallback;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
@Slf4j
public class RabbitMQSender implements DataCallback {
    private final RabbitTemplate rabbitTemplate;



    @Override
    public void onData(byte[] data, String uuid) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String dataString = objectMapper.writeValueAsString(data);
            Map<String, Object> dataMap = MapUtil.newHashMap();
            dataMap.put("uuid", uuid);
            dataMap.put("data", dataString);
            rabbitTemplate.convertAndSend(RabbitMQConfig.VIDEO_EXCHANGE, RabbitMQConfig.VIDEO_KEY, dataMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
