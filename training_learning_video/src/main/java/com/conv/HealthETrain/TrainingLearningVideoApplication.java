package com.conv.HealthETrain;
import com.conv.HealthETrain.client.LessonClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@LoadBalancerClients
@MapperScan("com.conv.HealthETrain.mapper")
@EnableFeignClients(clients = {LessonClient.class})
@EnableDiscoveryClient
public class    TrainingLearningVideoApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrainingLearningVideoApplication.class, args);
    }

}
