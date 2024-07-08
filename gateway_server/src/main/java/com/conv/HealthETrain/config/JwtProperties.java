package com.conv.HealthETrain.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.core.io.Resource;

import java.time.Duration;

/**
 * @author liusg
 */
@Data
@ConfigurationProperties(prefix = "healthe.jwt")
public class JwtProperties {
    private Resource location;
    private String password;
    private String alias;
    private Duration tokenTTL;
}
