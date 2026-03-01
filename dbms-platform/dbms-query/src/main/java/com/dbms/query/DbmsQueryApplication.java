package com.dbms.query;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * DBMS Query 模块启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.dbms.query.mapper")
public class DbmsQueryApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(DbmsQueryApplication.class, args);
    }
}
