package com.dbms.mask.controller;

import com.dbms.common.dto.Result;
import com.dbms.mask.dto.MaskRuleDTO;
import com.dbms.mask.entity.MaskRule;
import com.dbms.mask.service.DataMaskService;
import com.dbms.mask.service.MaskRuleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 脱敏规则管理控制器
 */
@RestController
@RequestMapping("/api/mask/rules")
public class MaskRuleController {
    
    @Autowired
    private MaskRuleService maskRuleService;
    
    /**
     * 获取所有脱敏规则
     */
    @GetMapping
    public Result<List<MaskRule>> listRules() {
        List<MaskRule> rules = maskRuleService.listAllRules();
        return Result.success(rules);
    }
    
    /**
     * 根据ID获取脱敏规则
     */
    @GetMapping("/{id}")
    public Result<MaskRule> getRule(@PathVariable Long id) {
        MaskRule rule = maskRuleService.getById(id);
        if (rule == null) {
            return Result.error("规则不存在");
        }
        return Result.success(rule);
    }
    
    /**
     * 根据数据类型获取脱敏规则
     */
    @GetMapping("/type/{dataType}")
    public Result<MaskRule> getRuleByDataType(@PathVariable String dataType) {
        MaskRule rule = maskRuleService.getRuleByDataType(dataType);
        return Result.success(rule);
    }
    
    /**
     * 创建脱敏规则
     */
    @PostMapping
    public Result<MaskRule> createRule(@Valid @RequestBody MaskRuleDTO dto) {
        MaskRule rule = maskRuleService.createRule(dto);
        return Result.success(rule);
    }
    
    /**
     * 更新脱敏规则
     */
    @PutMapping("/{id}")
    public Result<MaskRule> updateRule(@PathVariable Long id, @Valid @RequestBody MaskRuleDTO dto) {
        MaskRule rule = maskRuleService.updateRule(id, dto);
        return Result.success(rule);
    }
    
    /**
     * 删除脱敏规则
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteRule(@PathVariable Long id) {
        boolean removed = maskRuleService.deleteRule(id);
        if (removed) {
            return Result.success(null);
        }
        return Result.error("删除失败");
    }
    
    /**
     * 启用/禁用规则
     */
    @PatchMapping("/{id}/status")
    public Result<Void> toggleRuleStatus(@PathVariable Long id, @RequestParam Boolean enabled) {
        MaskRule rule = maskRuleService.getById(id);
        if (rule == null) {
            return Result.error("规则不存在");
        }
        rule.setEnabled(enabled);
        maskRuleService.updateById(rule);
        return Result.success(null);
    }
}
