package com.conv.HealthETrain;

import com.conv.HealthETrain.config.DefaultFeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author liusg
 */
@SpringBootApplication
@LoadBalancerClients
@MapperScan("com.conv.HealthETrain.mapper")
@EnableFeignClients(defaultConfiguration = DefaultFeignConfig.class)
@EnableDiscoveryClient
public class InformationPortalApplication {
    public static void main(String[] args) {
        SpringApplication.run(InformationPortalApplication.class, args);
    }
}
