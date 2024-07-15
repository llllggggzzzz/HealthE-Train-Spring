package com.conv.HealthETrain;


import com.conv.HealthETrain.client.InformationPortalClient;
import com.conv.HealthETrain.client.LessonClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@LoadBalancerClients
@MapperScan(value = "com.conv.HealthETrain.mapper")
@EnableFeignClients(clients = {LessonClient.class, InformationPortalClient.class})
public class ExamEvaluationApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExamEvaluationApplication.class, args);
    }
}
