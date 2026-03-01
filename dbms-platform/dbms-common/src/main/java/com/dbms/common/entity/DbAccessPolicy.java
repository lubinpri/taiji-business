package com.dbms.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据库访问策略
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("db_access_policy")
public class DbAccessPolicy extends BaseEntity {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 实例ID
     */
    private Long instanceId;
    
    /**
     * 库名(空表示全库)
     */
    private String catalogName;
    
    /**
     * 表名(空表示全表)
     */
    private String tableName;
    
    /**
     * 访问类型: read/write/admin
     */
    private String accessType;
    
    /**
     * 脱敏级别: none/low/high
     */
    private String maskLevel;
}
