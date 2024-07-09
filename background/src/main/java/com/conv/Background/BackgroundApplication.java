package com.conv.Background;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@LoadBalancerClients
@MapperScan(value = "com.conv.Background.mapper")
@EnableFeignClients
public class BackgroundApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackgroundApplication.class, args);
    }
}