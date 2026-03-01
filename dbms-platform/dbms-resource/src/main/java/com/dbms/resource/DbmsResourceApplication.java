package com.dbms.resource;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = "com.dbms")
@EnableDiscoveryClient
@MapperScan("com.dbms.resource.mapper")
public class DbmsResourceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(DbmsResourceApplication.class, args);
    }
}
