package com.conv.HealthETrain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author liusg
 */
@Component
@Data
@ConfigurationProperties(prefix = "healthe.auth")
public class AuthProperties {
    private List<String> includePaths;
    private List<String> excludePaths;
}
