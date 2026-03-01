package com.dbms.backup.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 备份任务实体
 */
@Data
@TableName("backup_task")
public class BackupTask {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 策略ID
     */
    private Long policyId;
    
    /**
     * 任务状态: pending/running/success/failed
     */
    private String taskStatus;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 备份文件路径
     */
    private String backupFilePath;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
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
