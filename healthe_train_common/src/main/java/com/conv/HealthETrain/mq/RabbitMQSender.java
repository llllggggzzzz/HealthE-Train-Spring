package com.conv.HealthETrain.mq;

import com.conv.HealthETrain.callback.DataCallback;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RabbitMQSender implements DataCallback {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void onData(byte[] data) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.VIDEO_EXCHANGE, RabbitMQConfig.VIDEO_KEY, data);
    }
}
