package com.conv.HealthETrain;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
@LoadBalancerClients
@MapperScan("com.conv.HealthETrain.mapper")
@EnableFeignClients
@EnableDiscoveryClient
public class TrainingLearningVideoApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrainingLearningVideoApplication.class, args);
    }

}
