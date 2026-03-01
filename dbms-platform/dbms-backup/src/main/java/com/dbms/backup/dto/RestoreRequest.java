package com.dbms.backup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 恢复请求DTO
 */
@Data
public class RestoreRequest {
    
    /**
     * 任务ID
     */
    @NotNull(message = "任务ID不能为空")
    private Long taskId;
    
    /**
     * 目标实例ID
     */
    @NotNull(message = "目标实例ID不能为空")
    private Long targetInstanceId;
    
    /**
     * 目标数据库名称
     */
    private String targetDatabase;
}
