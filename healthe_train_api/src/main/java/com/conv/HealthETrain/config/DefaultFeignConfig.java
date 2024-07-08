package com.conv.HealthETrain.config;

import com.conv.HealthETrain.utils.UserContext;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

/**
 * @author liusg
 */
public class DefaultFeignConfig {

    @Bean
    public RequestInterceptor userInfoRequestInterceptor() {
        return requestTemplate -> {
            Long userId = UserContext.getUser();
            if (userId != null) {
                requestTemplate.header("user-info", userId.toString());
            }
        };
    }
}
