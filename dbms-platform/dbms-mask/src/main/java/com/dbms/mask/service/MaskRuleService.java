package com.dbms.mask.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dbms.mask.dto.MaskRuleDTO;
import com.dbms.mask.entity.MaskRule;

import java.util.List;

/**
 * 脱敏规则服务接口
 */
public interface MaskRuleService extends IService<MaskRule> {
    
    /**
     * 创建脱敏规则
     * @param dto 规则DTO
     * @return 创建的规则
     */
    MaskRule createRule(MaskRuleDTO dto);
    
    /**
     * 更新脱敏规则
     * @param id 规则ID
     * @param dto 规则DTO
     * @return 更新后的规则
     */
    MaskRule updateRule(Long id, MaskRuleDTO dto);
    
    /**
     * 删除脱敏规则
     * @param id 规则ID
     * @return 是否删除成功
     */
    boolean deleteRule(Long id);
    
    /**
     * 根据数据类型获取启用的脱敏规则
     * @param dataType 数据类型
     * @return 脱敏规则
     */
    MaskRule getRuleByDataType(String dataType);
    
    /**
     * 获取所有脱敏规则
     * @return 规则列表
     */
    List<MaskRule> listAllRules();
}
