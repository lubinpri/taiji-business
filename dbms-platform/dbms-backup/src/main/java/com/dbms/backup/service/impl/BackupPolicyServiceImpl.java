package com.dbms.backup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dbms.backup.dto.BackupPolicyRequest;
import com.dbms.backup.entity.BackupPolicy;
import com.dbms.backup.mapper.BackupPolicyMapper;
import com.dbms.backup.service.BackupPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 备份策略服务实现
 */
@Service
@RequiredArgsConstructor
public class BackupPolicyServiceImpl extends ServiceImpl<BackupPolicyMapper, BackupPolicy> 
    implements BackupPolicyService {
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createPolicy(BackupPolicyRequest request, String createdBy) {
        BackupPolicy policy = new BackupPolicy();
        policy.setPolicyName(request.getPolicyName());
        policy.setInstanceId(request.getInstanceId());
        policy.setBackupType(request.getBackupType());
        policy.setScheduleCron(request.getScheduleCron());
        policy.setRetainDays(request.getRetainDays());
        policy.setEnabled(request.getEnabled());
        policy.setCreatedBy(createdBy);
        
        return this.save(policy);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePolicy(Long id, BackupPolicyRequest request, String updatedBy) {
        BackupPolicy policy = this.getById(id);
        if (policy == null) {
            throw new RuntimeException("策略不存在");
        }
        
        policy.setPolicyName(request.getPolicyName());
        policy.setBackupType(request.getBackupType());
        policy.setScheduleCron(request.getScheduleCron());
        policy.setRetainDays(request.getRetainDays());
        policy.setEnabled(request.getEnabled());
        policy.setUpdatedBy(updatedBy);
        
        return this.updateById(policy);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePolicy(Long id) {
        return this.removeById(id);
    }
    
    @Override
    public BackupPolicy getPolicyDetail(Long id) {
        return this.getById(id);
    }
    
    @Override
    public Page<BackupPolicy> pagePolicy(Page<BackupPolicy> page, String policyName, Long instanceId) {
        LambdaQueryWrapper<BackupPolicy> wrapper = new LambdaQueryWrapper<>();
        if (policyName != null && !policyName.isEmpty()) {
            wrapper.like(BackupPolicy::getPolicyName, policyName);
        }
        if (instanceId != null) {
            wrapper.eq(BackupPolicy::getInstanceId, instanceId);
        }
        wrapper.orderByDesc(BackupPolicy::getCreatedTime);
        
        return this.page(page, wrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean togglePolicy(Long id, Integer enabled) {
        BackupPolicy policy = this.getById(id);
        if (policy == null) {
            throw new RuntimeException("策略不存在");
        }
        
        policy.setEnabled(enabled);
        return this.updateById(policy);
    }
    
    @Override
    public java.util.List<BackupPolicy> getEnabledPolicies() {
        return baseMapper.findEnabledPolicies();
    }
}
