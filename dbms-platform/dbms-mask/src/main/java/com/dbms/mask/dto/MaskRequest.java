package com.dbms.mask.dto;

import lombok.Data;

/**
 * 脱敏请求DTO
 */
@Data
public class MaskRequest {
    
    /**
     * 数据类型
     */
    private String dataType;
    
    /**
     * 原始数据
     */
    private String data;
    
    /**
     * 算法类型
     */
    private String algorithm;
    
    /**
     * 脱敏配置 (JSON格式)
     */
    private String config;
}
