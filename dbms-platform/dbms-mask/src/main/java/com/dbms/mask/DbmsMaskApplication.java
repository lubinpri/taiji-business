package com.dbms.mask;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 数据脱敏服务启动类
 */
@SpringBootApplication
@MapperScan("com.dbms.mask.mapper")
public class DbmsMaskApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(DbmsMaskApplication.class, args);
    }
}
