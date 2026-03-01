package com.dbms.permission.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 数据库实例实体（从dbms-resource模块引用）
 */
@Data
@TableName("db_instance")
public class DbInstance {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("instance_name")
    private String instanceName;
    
    @TableField("instance_type")
    private String instanceType;
    
    private String host;
    
    private Integer port;
    
    @TableField("service_name")
    private String serviceName;
    
    private String username;
    
    private String password;
    
    private String description;
    
    private Integer status;
    
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
