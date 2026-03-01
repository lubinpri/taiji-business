package com.dbms.backup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 备份策略请求DTO
 */
@Data
public class BackupPolicyRequest {
    
    /**
     * 策略名称
     */
    @NotBlank(message = "策略名称不能为空")
    private String policyName;
    
    /**
     * 实例ID
     */
    @NotNull(message = "实例ID不能为空")
    private Long instanceId;
    
    /**
     * 备份类型: full/increment
     */
    @NotBlank(message = "备份类型不能为空")
    private String backupType;
    
    /**
     * 调度cron表达式
     */
    private String scheduleCron;
    
    /**
     * 保留天数
     */
    private Integer retainDays = 30;
    
    /**
     * 是否启用
     */
    private Integer enabled = 1;
}
