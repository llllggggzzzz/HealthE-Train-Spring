package com.conv.HealthETrain.config;


import cn.hutool.core.util.StrUtil;
import com.conv.HealthETrain.controller.VideoSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSocket
@Slf4j
public class WebSocketConfig implements WebSocketConfigurer {

    private static Map<String, String> parseQueryString(String query) {
        Map<String, String> queryParams = new HashMap<>();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair: pairs) {
                // 遍历提取key and value
                String[] split = pair.split("=");
                if(split.length != 2) {
                    continue;
                }
                queryParams.put(split[0], split[1]);
            }
        }
        return queryParams;
    }


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new VideoSocketHandler(), "/video")
                //设置处理类和连接路径
                .setAllowedOrigins("*") //设置作用域
                .addInterceptors(new MyWebSocketInterceptor());//设置拦截器
    }


    /**
     * 自定义拦截器拦截WebSocket请求
     */
    static class MyWebSocketInterceptor implements HandshakeInterceptor {

        @Override
        public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
            String query = request.getURI().getQuery();
            String key = StrUtil.emptyIfNull(parseQueryString(query).get("key"));
            String hostAddress = request.getRemoteAddress().getAddress().getHostAddress();
            log.info("检测到客户端连接: {}, Key:{}", hostAddress, key);
            attributes.put("key", key);
            return true;
        }

        @Override
        public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
            log.info("attributes添加完毕");
        }
    }

}

