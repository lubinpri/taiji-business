package com.dbms.query.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 可视化查询构建请求
 */
@Data
public class QueryVisualizeRequest {
    
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
     * 表名
     */
    @NotBlank(message = "表名不能为空")
    private String tableName;
    
    /**
     * 要查询的列 (* 表示全部)
     */
    private List<String> columns;
    
    /**
     * 查询条件
     */
    private List<WhereCondition> whereConditions;
    
    /**
     * 排序字段
     */
    private List<OrderByCondition> orderBy;
    
    /**
     * 分组字段
     */
    private List<String> groupBy;
    
    /**
     * 分页参数
     */
    private Integer pageNum = 1;
    
    private Integer pageSize = 100;
    
    /**
     * WHERE条件
     */
    @Data
    public static class WhereCondition {
        private String column;
        private String operator; // =, !=, >, <, >=, <=, LIKE, IN, BETWEEN, IS NULL, IS NOT NULL
        private Object value;
        private Object value2; // 用于BETWEEN
        private String logic; // AND, OR
    }
    
    /**
     * 排序条件
     */
    @Data
    public static class OrderByCondition {
        private String column;
        private String direction; // ASC, DESC
    }
}
