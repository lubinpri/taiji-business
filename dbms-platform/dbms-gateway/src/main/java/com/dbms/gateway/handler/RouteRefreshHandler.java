package com.dbms.gateway.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.handler.RoutePredicateHandlerMapping;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * 路由刷新处理器
 * 当Nacos配置中心的路由配置发生变化时，自动刷新网关路由
 */
@Slf4j
@Component
public class RouteRefreshHandler {

    @Autowired
    private RoutePredicateHandlerMapping routePredicateHandlerMapping;

    /**
     * 刷新路由
     */
    public Mono<Void> refreshRoute() {
        return routePredicateHandlerMapping.refresh()
                .then(Mono.defer(() -> {
                    log.info("路由刷新成功");
                    return Mono.empty();
                }))
                .onErrorResume(e -> {
                    log.error("路由刷新失败: {}", e.getMessage());
                    return Mono.error(e);
                });
    }

    /**
     * 获取所有当前路由信息
     */
    public Map<String, Route> getRoutes() {
        return routePredicateHandlerMapping.getRoutes()
                .collectMap(Route::getId)
                .block();
    }
}
