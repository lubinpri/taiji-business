package com.dbms.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 表信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableInfo {
    
    /**
     * 表名
     */
    private String tableName;
    
    /**
     * 表注释
     */
    private String comment;
    
    /**
     * 引擎
     */
    private String engine;
    
    /**
     * 行数
     */
    private Long rowCount;
    
    /**
     * 数据大小(字节)
     */
    private Long dataLength;
    
    /**
     * 索引大小(字节)
     */
    private Long indexLength;
    
    /**
     * 创建时间
     */
    private String createTime;
    
    /**
     * 列信息
     */
    private List<ColumnInfo> columns;
}
