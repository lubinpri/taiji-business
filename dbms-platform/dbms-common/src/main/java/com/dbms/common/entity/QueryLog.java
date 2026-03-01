package com.dbms.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询日志
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("query_log")
public class QueryLog extends BaseEntity {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 实例ID
     */
    private Long instanceId;
    
    /**
     * SQL文本
     */
    private String sqlText;
    
    /**
     * 耗时(ms)
     */
    private Integer queryTime;
    
    /**
     * 返回行数
     */
    private Integer resultRows;
    
    /**
     * IP地址
     */
    private String ipAddress;
    
    /**
     * 用户代理
     */
    private String userAgent;
    
    /**
     * 水印信息
     */
    private String watermarkInfo;
}
