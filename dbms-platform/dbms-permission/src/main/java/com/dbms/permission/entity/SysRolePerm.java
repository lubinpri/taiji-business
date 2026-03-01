package com.dbms.permission.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 角色权限关联实体
 */
@Data
@TableName("sys_role_perm")
public class SysRolePerm {
    
    private Long roleId;
    
    private Long permId;
    
    private String createdBy;
    
    private LocalDateTime createdTime;
}
