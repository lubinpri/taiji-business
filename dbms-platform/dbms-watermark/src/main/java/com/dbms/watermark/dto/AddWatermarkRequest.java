package com.dbms.watermark.dto;

import lombok.Data;

/**
 * 查询结果添加水印请求DTO
 */
@Data
public class AddWatermarkRequest {
    
    /**
     * 数据类型: TEXT, EXCEL, PDF, JSON
     */
    private String dataType;
    
    /**
     * 原始数据内容
     */
    private String dataContent;
    
    /**
     * 水印类型: TEXT, IMAGE, INFINITE
     */
    private String watermarkType;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 用户名称
     */
    private String userName;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 客户端IP
     */
    private String clientIp;
    
    /**
     * 水印文本（可选，默认使用用户信息生成）
     */
    private String watermarkText;
}
