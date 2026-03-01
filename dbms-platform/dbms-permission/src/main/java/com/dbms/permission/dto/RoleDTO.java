package com.dbms.permission.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色DTO
 */
@Data
public class RoleDTO {
    private Long id;
    private String roleCode;
    private String roleName;
    private String description;
    private List<Long> permissionIds;
    private List<String> permissionCodes;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;
}
