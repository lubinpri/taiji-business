package com.dbms.permission.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 数据库访问策略实体（细粒度权限）
 */
@Data
@TableName("db_access_policy")
public class DbAccessPolicy {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("instance_id")
    private Long instanceId;
    
    @TableField("catalog_name")
    private String catalogName;
    
    @TableField("table_name")
    private String tableName;
    
    @TableField("access_type")
    private String accessType;
    
    @TableField("mask_level")
    private String maskLevel;
    
    @TableField("created_by")
    private String createdBy;
    
    @TableField("created_time")
    private LocalDateTime createdTime;
    
    @TableField("updated_by")
    private String updatedBy;
    
    @TableField("updated_time")
    private LocalDateTime updatedTime;
    
    @TableLogic
    private Integer deleted;
}
