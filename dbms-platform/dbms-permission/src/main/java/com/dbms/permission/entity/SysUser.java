package com.dbms.permission.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 系统用户实体
 */
@Data
@TableName("sys_user")
public class SysUser {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String password;
    
    @TableField("real_name")
    private String realName;
    
    private String email;
    
    private String phone;
    
    @TableField("dept_id")
    private Long deptId;
    
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
