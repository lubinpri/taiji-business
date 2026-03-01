package com.dbms.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 脱敏规则
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mask_rule")
public class MaskRule extends BaseEntity {
    
    /**
     * 规则名称
     */
    private String ruleName;
    
    /**
     * 数据类型: idcard/phone/bank/card/email
     */
    private String dataType;
    
    /**
     * 算法: mask/replace/encrypt/hash
     */
    private String maskAlgorithm;
    
    /**
     * 脱敏配置JSON
     */
    private String maskConfig;
}
