package com.dbms.common.dto;

import lombok.Data;

/**
 * 查询请求
 */
@Data
public class QueryRequest {
    
    /**
     * 实例ID
     */
    private Long instanceId;
    
    /**
     * SQL语句
     */
    private String sql;
    
    /**
     * 库名
     */
    private String catalog;
    
    /**
     * 表名
     */
    private String table;
    
    /**
     * 查询条件
     */
    private String whereClause;
    
    /**
     * 分页页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页条数
     */
    private Integer pageSize = 100;
}
