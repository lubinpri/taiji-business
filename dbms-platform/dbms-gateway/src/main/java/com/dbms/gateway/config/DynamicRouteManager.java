package com.dbms.gateway.config;

import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 动态路由管理器
 * 支持从Nacos配置中心动态加载路由
 */
@Slf4j
@Component
public class DynamicRouteManager {

    private final RouteDefinitionWriter routeDefinitionWriter;
    private final ApplicationEventPublisher publisher;

    public DynamicRouteManager(RouteDefinitionWriter routeDefinitionWriter,
                               ApplicationEventPublisher publisher) {
        this.routeDefinitionWriter = routeDefinitionWriter;
        this.publisher = publisher;
    }

    /**
     * 添加路由
     */
    public Mono<Void> addRoute(RouteDefinition definition) {
        return routeDefinitionWriter.save(Mono.just(definition))
                .then(Mono.defer(() -> {
                    publisher.publishEvent(new RefreshRoutesEvent(this));
                    log.info("路由添加成功: {}", definition.getId());
                    return Mono.empty();
                }));
    }

    /**
     * 批量添加路由
     */
    public Mono<Void> addRoutes(List<RouteDefinition> definitions) {
        return routeDefinitionWriter.save(Mono.just(definitions.stream()
                        .reduce((d1, d2) -> {
                            d1.getFilters().addAll(d2.getFilters());
                            d1.getPredicates().addAll(d2.getPredicates());
                            return d1;
                        })
                        .orElse(null)))
                .then(Mono.defer(() -> {
                    publisher.publishEvent(new RefreshRoutesEvent(this));
                    log.info("批量路由添加成功: {} 条", definitions.size());
                    return Mono.empty();
                }));
    }

    /**
     * 更新路由
     */
    public Mono<Void> updateRoute(RouteDefinition definition) {
        return routeDefinitionWriter.delete(Mono.just(definition.getId()))
                .then(addRoute(definition));
    }

    /**
     * 删除路由
     */
    public Mono<Void> deleteRoute(String routeId) {
        return routeDefinitionWriter.delete(Mono.just(routeId))
                .then(Mono.defer(() -> {
                    publisher.publishEvent(new RefreshRoutesEvent(this));
                    log.info("路由删除成功: {}", routeId);
                    return Mono.empty();
                }));
    }

    /**
     * 刷新所有路由
     */
    public void refreshRoutes() {
        publisher.publishEvent(new RefreshRoutesEvent(this));
        log.info("路由刷新成功");
    }
}
