package com.dbms.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * 请求日志过滤器 - 记录所有请求的详细信息
 */
@Slf4j
@Component
public class RequestLogFilter implements GlobalFilter, Ordered {

    private static final String START_TIME = "startTime";
    private static final String REQUEST_ID = "requestId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        // 记录请求开始时间
        exchange.getAttributes().put(START_TIME, Instant.now());
        exchange.getAttributes().put(REQUEST_ID, requestId);

        // 添加requestId到响应头
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().add("X-Request-Id", requestId);

        // 打印请求日志
        log.info(">>> [{}] {} {} | IP: {} | User-Agent: {}",
                requestId,
                request.getMethod(),
                request.getURI().getPath(),
                getClientIpAddress(request),
                request.getHeaders().getFirst("User-Agent"));

        // 继续执行过滤器链
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            // 请求完成后记录响应日志
            Instant startTime = exchange.getAttribute(START_TIME);
            if (startTime != null) {
                long duration = Duration.between(startTime, Instant.now()).toMillis();
                log.info("<<< [{}] {} {} | Status: {} | Duration: {}ms",
                        requestId,
                        request.getMethod(),
                        request.getURI().getPath(),
                        response.getStatusCode(),
                        duration);
            }
        }));
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(ServerHttpRequest request) {
        String ip = request.getHeaders().getFirst("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddress() != null ?
                    request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
        }
        // 多级代理时取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    @Override
    public int getOrder() {
        // 最先执行
        return -200;
    }
}
