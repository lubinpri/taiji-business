package com.dbms.permission;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 权限管理服务启动类
 */
@SpringBootApplication
@MapperScan("com.dbms.permission.mapper")
public class DbmsPermissionApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(DbmsPermissionApplication.class, args);
    }
}
