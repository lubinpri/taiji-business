package com.dbms.backup;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.dbms.backup.mapper")
@EnableScheduling
public class DbmsBackupApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(DbmsBackupApplication.class, args);
    }
}
