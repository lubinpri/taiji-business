package com.dbms.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 列信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColumnInfo {
    
    /**
     * 列名
     */
    private String name;
    
    /**
     * 列类型
     */
    private String type;
    
    /**
     * 是否可为空
     */
    private Boolean nullable;
    
    /**
     * 默认值
     */
    private String defaultValue;
    
    /**
     * 注释
     */
    private String comment;
    
    /**
     * 键类型 (PRI, UNI, MUL)
     */
    private String keyType;
    
    /**
     * 字符集
     */
    private String charset;
    
    /**
     * 字段长度
     */
    private Integer length;
    
    /**
     * 小数位数
     */
    private Integer decimalDigits;
}
