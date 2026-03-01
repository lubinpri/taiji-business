package com.dbms.mask.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 脱敏规则DTO
 */
@Data
public class MaskRuleDTO {
    
    private Long id;
    
    @NotBlank(message = "规则名称不能为空")
    private String ruleName;
    
    @NotBlank(message = "数据类型不能为空")
    private String dataType;
    
    @NotBlank(message = "算法不能为空")
    private String maskAlgorithm;
    
    private String maskConfig;
    
    private String description;
    
    private Boolean enabled;
}
