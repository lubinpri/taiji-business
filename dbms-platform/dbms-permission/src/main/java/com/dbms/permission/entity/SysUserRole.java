package com.dbms.permission.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户角色关联实体
 */
@Data
@TableName("sys_user_role")
public class SysUserRole {
    
    private Long userId;
    
    private Long roleId;
    
    private String createdBy;
    
    private LocalDateTime createdTime;
}
