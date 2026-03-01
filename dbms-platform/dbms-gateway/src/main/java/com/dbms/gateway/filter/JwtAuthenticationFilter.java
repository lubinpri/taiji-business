package com.dbms.gateway.filter;

import com.dbms.gateway.config.JwtConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * JWT认证过滤器 - 全局拦截器
 * 验证请求中的JWT Token
 */
@Slf4j
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtConfig jwtConfig;

    // 无需认证的路径
    private static final List<String> WHITELIST = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/captcha",
            "/actuator/health"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 白名单路径直接放行
        if (isWhitelisted(path)) {
            return chain.filter(exchange);
        }

        // 获取Token
        String token = getTokenFromRequest(request);

        if (!StringUtils.hasText(token)) {
            log.warn("请求路径: {}, 缺少认证Token", path);
            return unauthorized(exchange.getResponse(), "缺少认证Token");
        }

        // 验证Token
        if (!jwtConfig.validateToken(token)) {
            log.warn("请求路径: {}, Token无效或已过期", path);
            return unauthorized(exchange.getResponse(), "Token无效或已过期");
        }

        // 将用户信息添加到请求头，传递给下游服务
        String username = jwtConfig.getUsernameFromToken(token);
        String userId = jwtConfig.getUserIdFromToken(token);
        String roles = jwtConfig.getRolesFromToken(token);

        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-User-Id", userId != null ? userId : "")
                .header("X-Username", username != null ? username : "")
                .header("X-User-Roles", roles != null ? roles : "")
                .build();

        log.debug("请求路径: {}, 用户: {}, 角色: {}", path, username, roles);

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    /**
     * 从请求头获取Token
     */
    private String getTokenFromRequest(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 检查路径是否在白名单
     */
    private boolean isWhitelisted(String path) {
        return WHITELIST.stream().anyMatch(path::startsWith);
    }

    /**
     * 返回401未授权响应
     */
    private Mono<Void> unauthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        String body = String.format("{\"code\":401,\"message\":\"%s\"}", message);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    @Override
    public int getOrder() {
        // 在其他过滤器之前执行
        return -100;
    }
}
