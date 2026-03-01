package com.dbms.gateway.filter;

import com.dbms.gateway.config.RateLimitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * 限流过滤器 - 基于Redis的令牌桶算法
 */
@Slf4j
@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

    @Autowired
    private RateLimitConfig rateLimitConfig;

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    private static final String RATE_LIMIT_KEY_PREFIX = "gateway:rate_limit:";
    private static final String RATE_LIMIT_SCRIPT =
            "local key = KEYS[1] " +
                    "local limit = tonumber(ARGV[1]) " +
                    "local current = redis.call('get', key) " +
                    "if current then " +
                    "  if tonumber(current) >= limit then " +
                    "    return 0 " +
                    "  end " +
                    "  return redis.call('incr', key) " +
                    "end " +
                    "redis.call('setex', key, 1, 1) " +
                    "return 1";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!rateLimitConfig.isEnabled()) {
            return chain.filter(exchange);
        }

        // 获取请求路径作为限流Key
        String path = exchange.getRequest().getURI().getPath();
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
        String rateLimitKey = RATE_LIMIT_KEY_PREFIX + (userId != null ? userId : "anonymous") + ":" + path;

        // 使用Lua脚本实现原子性限流
        return redisTemplate.execute(
                        org.springframework.data.redis.core.script.DefaultRedisScript.builder()
                                .setScriptText(RATE_LIMIT_SCRIPT)
                                .build(),
                        java.util.Collections.singletonList(rateLimitKey),
                        String.valueOf(rateLimitConfig.getDefaultLimit())
                )
                .next()
                .flatMap(result -> {
                    if ("1".equals(String.valueOf(result))) {
                        return chain.filter(exchange);
                    } else {
                        log.warn("请求被限流: path={}, userId={}", path, userId);
                        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                        exchange.getResponse().getHeaders().add("Content-Type", "application/json;charset=UTF-8");
                        String body = "{\"code\":429,\"message\":\"请求过于频繁，请稍后再试\"}";
                        return exchange.getResponse().writeWith(
                                Mono.just(exchange.getResponse().bufferFactory().wrap(body.getBytes()))
                        );
                    }
                })
                .defaultIfEmpty(chain.filter(exchange).then(Mono.empty()));
    }

    @Override
    public int getOrder() {
        // 在JWT认证之后执行
        return -90;
    }
}
