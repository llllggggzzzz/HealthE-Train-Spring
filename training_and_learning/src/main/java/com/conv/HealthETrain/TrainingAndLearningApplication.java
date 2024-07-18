package com.conv.HealthETrain;

import com.conv.HealthETrain.client.*;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@LoadBalancerClients
@MapperScan("com.conv.HealthETrain.mapper")
@EnableFeignClients(clients =
        {InformationPortalClient.class, VideoClient.class, ExamQuestionClient.class, CoursewareClient.class})
@EnableDiscoveryClient
public class TrainingAndLearningApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrainingAndLearningApplication.class, args);
    }
}
