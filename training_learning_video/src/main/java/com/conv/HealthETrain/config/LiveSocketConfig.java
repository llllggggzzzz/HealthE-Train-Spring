package com.conv.HealthETrain.config;


import cn.hutool.core.util.StrUtil;
import com.conv.HealthETrain.controller.LiveSocketHandler;
import com.conv.HealthETrain.controller.VideoSocketHandler;
import com.conv.HealthETrain.mq.RabbitMQSender;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSocket
@Slf4j
@AllArgsConstructor
public class LiveSocketConfig implements WebSocketConfigurer {

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
        registry.addHandler(new LiveSocketHandler(), "/live")
                //设置处理类和连接路径
                .setAllowedOrigins("*") //设置作用域
                .addInterceptors(new LiveWebSocketInterceptor());//设置拦截器
    }


    /**
     * 自定义拦截器拦截WebSocket请求
     */
    static class LiveWebSocketInterceptor implements HandshakeInterceptor {

        @Override
        public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
            String query = request.getURI().getQuery();
            String userId = StrUtil.emptyIfNull(parseQueryString(query).get("userId"));
            String streamId = StrUtil.emptyIfNull(parseQueryString(query).get("streamId"));
            String hostAddress = request.getRemoteAddress().getAddress().getHostAddress();
            log.info("检测到客户端进入直播间: {}, user:{}", hostAddress, userId);
            attributes.put("userId", userId);
            attributes.put("streamId", streamId);
            return true;
        }

        @Override
        public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
            log.info("attributes添加完毕");
        }
    }
}
