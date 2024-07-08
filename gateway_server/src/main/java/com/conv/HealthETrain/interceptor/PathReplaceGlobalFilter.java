//package com.conv.HealthETrain.interceptor;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
///**
// * @author liusg
// */
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class PathReplaceGlobalFilter implements GlobalFilter, Ordered {
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        ServerHttpRequest request = exchange.getRequest();
//        String path = request.getURI().getPath();
//
//        log.info("path: {}", path);
//
//        if (path.startsWith("/api/v1/")) {
//            String newPath = path.replaceFirst("/api/v1/", "/");
//            ServerHttpRequest newRequest = request.mutate().path(newPath).build();
//            return chain.filter(exchange.mutate().request(newRequest).build());
//        }
//
//        return chain.filter(exchange);
//    }
//
//    @Override
//    public int getOrder() {
//        return 0;
//    }
//}
