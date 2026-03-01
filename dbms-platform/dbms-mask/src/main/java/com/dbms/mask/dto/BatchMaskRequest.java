package com.dbms.mask.dto;

import lombok.Data;
import java.util.List;

/**
 * 批量脱敏请求DTO
 */
@Data
public class BatchMaskRequest {
    
    /**
     * 数据类型
     */
    private String dataType;
    
    /**
     * 数据列表
     */
    private List<String> dataList;
    
    /**
     * 算法类型
     */
    private String algorithm;
    
    /**
     * 脱敏配置 (JSON格式)
     */
    private String config;
}
