package com.dbms.permission.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限DTO
 */
@Data
public class PermissionDTO {
    private Long id;
    private String permCode;
    private String permName;
    private String resourceType;
    private Long parentId;
    private List<PermissionDTO> children;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;
}
