package com.conv.HealthETrain.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String VIDEO_EXCHANGE = "videoDataExchange";
    public static final String VIDEO_QUEUE = "videoDataQueue";
    public static final String VIDEO_KEY = "videoDataRoutingKey";

    @Bean
    public Queue videoDataQueue() {
        return new Queue(VIDEO_QUEUE, true);
    }

    @Bean
    public DirectExchange videoDataExchange() {
        return new DirectExchange(VIDEO_EXCHANGE);
    }

    @Bean
    public Binding binding(Queue videoDataQueue, DirectExchange videoDataExchange) {
        return BindingBuilder.bind(videoDataQueue).to(videoDataExchange).with(VIDEO_KEY);
    }
}

