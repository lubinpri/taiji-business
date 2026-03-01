package com.dbms.permission.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 数据库访问策略DTO
 */
@Data
public class AccessPolicyDTO {
    private Long id;
    private Long userId;
    private String username;
    private Long instanceId;
    private String instanceName;
    private String catalogName;
    private String tableName;
    private String accessType;
    private String maskLevel;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;
}
