package com.dbms.query.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * SQL查询执行请求
 */
@Data
public class QueryExecuteRequest {
    
    /**
     * 数据库实例ID
     */
    @NotNull(message = "实例ID不能为空")
    private Long instanceId;
    
    /**
     * 数据库名称
     */
    @NotBlank(message = "数据库名称不能为空")
    private String database;
    
    /**
     * SQL语句
     */
    @NotBlank(message = "SQL语句不能为空")
    private String sql;
    
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页条数
     */
    private Integer pageSize = 100;
    
    /**
     * 额外参数
     */
    private Map<String, Object> params;
}
