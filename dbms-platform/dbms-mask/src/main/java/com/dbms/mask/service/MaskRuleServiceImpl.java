package com.dbms.mask.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dbms.mask.dto.MaskRuleDTO;
import com.dbms.mask.entity.MaskRule;
import com.dbms.mask.mapper.MaskRuleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 脱敏规则服务实现
 */
@Service
public class MaskRuleServiceImpl extends ServiceImpl<MaskRuleMapper, MaskRule> implements MaskRuleService {
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MaskRule createRule(MaskRuleDTO dto) {
        MaskRule rule = new MaskRule();
        rule.setRuleName(dto.getRuleName());
        rule.setDataType(dto.getDataType());
        rule.setMaskAlgorithm(dto.getMaskAlgorithm());
        rule.setMaskConfig(dto.getMaskConfig());
        rule.setDescription(dto.getDescription());
        rule.setEnabled(dto.getEnabled() != null ? dto.getEnabled() : true);
        rule.setCreateTime(LocalDateTime.now());
        rule.setUpdateTime(LocalDateTime.now());
        
        this.save(rule);
        return rule;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MaskRule updateRule(Long id, MaskRuleDTO dto) {
        MaskRule rule = this.getById(id);
        if (rule == null) {
            throw new RuntimeException("规则不存在: " + id);
        }
        
        rule.setRuleName(dto.getRuleName());
        rule.setDataType(dto.getDataType());
        rule.setMaskAlgorithm(dto.getMaskAlgorithm());
        rule.setMaskConfig(dto.getMaskConfig());
        rule.setDescription(dto.getDescription());
        if (dto.getEnabled() != null) {
            rule.setEnabled(dto.getEnabled());
        }
        rule.setUpdateTime(LocalDateTime.now());
        
        this.updateById(rule);
        return rule;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRule(Long id) {
        return this.removeById(id);
    }
    
    @Override
    public MaskRule getRuleByDataType(String dataType) {
        LambdaQueryWrapper<MaskRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaskRule::getDataType, dataType)
               .eq(MaskRule::getEnabled, true)
               .orderByDesc(MaskRule::getCreateTime)
               .last("LIMIT 1");
        return this.getOne(wrapper);
    }
    
    @Override
    public List<MaskRule> listAllRules() {
        LambdaQueryWrapper<MaskRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(MaskRule::getCreateTime);
        return this.list(wrapper);
    }
}
