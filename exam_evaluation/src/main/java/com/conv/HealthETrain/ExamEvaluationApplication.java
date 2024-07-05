package com.conv.HealthETrain;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@LoadBalancerClients
@MapperScan(value = "com.conv.HealthETrain.mapper")
@EnableFeignClients
public class ExamEvaluationApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExamEvaluationApplication.class, args);
    }
}
