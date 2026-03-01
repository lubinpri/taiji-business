package com.dbms.audit;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = "com.dbms")
@EnableDiscoveryClient
@MapperScan("com.dbms.audit.mapper")
public class DbmsAuditApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(DbmsAuditApplication.class, args);
    }
}
