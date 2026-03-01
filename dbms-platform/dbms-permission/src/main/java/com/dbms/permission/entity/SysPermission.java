package com.dbms.permission.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 权限实体
 */
@Data
@TableName("sys_permission")
public class SysPermission {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("perm_code")
    private String permCode;
    
    @TableField("perm_name")
    private String permName;
    
    @TableField("resource_type")
    private String resourceType;
    
    @TableField("parent_id")
    private Long parentId;
    
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
