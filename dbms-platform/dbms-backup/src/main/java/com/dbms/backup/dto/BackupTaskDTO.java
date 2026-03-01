package com.dbms.backup.dto;

import com.dbms.backup.entity.BackupPolicy;
import com.dbms.backup.entity.BackupTask;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 备份任务DTO
 */
@Data
public class BackupTaskDTO {
    
    /**
     * 任务ID
     */
    private Long id;
    
    /**
     * 策略ID
     */
    private Long policyId;
    
    /**
     * 任务状态
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
    private LocalDateTime createdTime;
    
    // ==================== 关联字段 ====================
    
    /**
     * 策略名称
     */
    private String policyName;
    
    /**
     * 备份类型
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
     * 实例名称
     */
    private String instanceName;
}
