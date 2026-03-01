package com.dbms.watermark.dto;

import lombok.Data;

import java.util.List;

/**
 * 水印信息DTO - 包含用户ID、时间戳、会话IDpublic class Watermark
 */
@Data
Info {
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 用户名称
     */
    private String userName;
    
    /**
     * 时间戳
     */
    private Long timestamp;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 客户端IP
     */
    private String clientIp;
    
    /**
     * 查询SQL（可选，用于审计）
     */
    private String querySql;
    
    public WatermarkInfo() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public WatermarkInfo(String userId, String sessionId) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * 生成水印文本
     */
    public String toWatermarkText() {
        return String.format("User:%s | Time:%s | Session:%s", 
                userId, 
                java.time.Instant.ofEpochMilli(timestamp).atZone(java.time.ZoneId.systemDefault()).toLocalDateTime(),
                sessionId);
    }
    
    /**
     * 生成简短水印文本
     */
    public String toShortText() {
        return String.format("%s|%s|%s", userId, timestamp, sessionId);
    }
}
