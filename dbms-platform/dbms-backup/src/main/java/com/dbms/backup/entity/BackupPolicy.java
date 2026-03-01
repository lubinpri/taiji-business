package com.dbms.backup.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 备份策略实体
 */
@Data
@TableName("backup_policy")
public class BackupPolicy {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 策略名称
     */
    private String policyName;
    
    /**
     * 实例ID
     */
    private Long instanceId;
    
    /**
     * 备份类型: full/increment
     */
    private String backupType;
    
    /**
     * 调度cron表达式
     */
    private String scheduleCron;
    
    /**
     * 保留天数
     */
    private Integer retainDays;
    
    /**
     * 是否启用: 0禁用 1启用
     */
    private Integer enabled;
    
    /**
     * 创建人
     */
    private String createdBy;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    /**
     * 更新人
     */
    private String updatedBy;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    
    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;
}
