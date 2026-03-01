package com.dbms.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 查询结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryResult {
    
    /**
     * 列信息
     */
    private List<ColumnInfo> columns;
    
    /**
     * 数据行
     */
    private List<Map<String, Object>> rows;
    
    /**
     * 总数
     */
    private Long total;
    
    /**
     * 页码
     */
    private Integer pageNum;
    
    /**
     * 每页条数
     */
    private Integer pageSize;
    
    /**
     * 执行时间(毫秒)
     */
    private Long executionTime;
    
    /**
     * 影响的行数(对于INSERT/UPDATE/DELETE)
     */
    private Integer affectedRows;
    
    /**
     * SQL类型
     */
    private String sqlType;
}
