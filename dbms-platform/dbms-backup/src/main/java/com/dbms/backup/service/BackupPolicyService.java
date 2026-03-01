package com.dbms.backup.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dbms.backup.dto.BackupPolicyRequest;
import com.dbms.backup.dto.BackupTaskDTO;
import com.dbms.backup.dto.RestoreRequest;
import com.dbms.backup.entity.BackupPolicy;
import com.dbms.backup.entity.BackupTask;

import java.util.List;
import java.util.Map;

/**
 * 备份策略服务
 */
public interface BackupPolicyService extends IService<BackupPolicy> {
    
    /**
     * 创建备份策略
     */
    boolean createPolicy(BackupPolicyRequest request, String createdBy);
    
    /**
     * 更新备份策略
     */
    boolean updatePolicy(Long id, BackupPolicyRequest request, String updatedBy);
    
    /**
     * 删除备份策略
     */
    boolean deletePolicy(Long id);
    
    /**
     * 获取策略详情
     */
    BackupPolicy getPolicyDetail(Long id);
    
    /**
     * 分页查询策略
     */
    Page<BackupPolicy> pagePolicy(Page<BackupPolicy> page, String policyName, Long instanceId);
    
    /**
     * 启用/禁用策略
     */
    boolean togglePolicy(Long id, Integer enabled);
    
    /**
     * 获取所有启用的策略
     */
    List<BackupPolicy> getEnabledPolicies();
}
