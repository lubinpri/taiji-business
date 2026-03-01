package com.dbms.permission.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户DTO
 */
@Data
public class UserDTO {
    private Long id;
    private String username;
    private String password;
    private String realName;
    private String email;
    private String phone;
    private Long deptId;
    private Integer status;
    private List<Long> roleIds;
    private List<String> roleCodes;
    private List<String> permissions;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;
}
