package com.dbms.watermark;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 水印服务启动类
 */
@SpringBootApplication
@MapperScan("com.dbms.watermark.mapper")
public class WatermarkApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(WatermarkApplication.class, args);
    }
}
