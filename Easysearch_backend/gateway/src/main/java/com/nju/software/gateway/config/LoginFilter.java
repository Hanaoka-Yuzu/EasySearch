package com.nju.software.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 *
 */
@Component
@Slf4j
public class LoginFilter implements GlobalFilter, Ordered {

    private final Set<String> whiteListPaths = Set.of("/user/login", "/user/register");


    /**
     * 执行过滤器中的业务逻辑
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        String requestPath = exchange.getRequest().getPath().toString();
//        if (whiteListPaths.contains(requestPath)) {
//            return chain.filter(exchange);
//        }
//        String token = exchange.getRequest().getQueryParams().getFirst("access-token");
//        if (token == null) {
//            log.info("没有登录");
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        }
        log.info("日志测试");
        return chain.filter(exchange);
    }

    /**
     * 指定过滤器的执行顺序 , 返回值越小执行优先级越高
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}