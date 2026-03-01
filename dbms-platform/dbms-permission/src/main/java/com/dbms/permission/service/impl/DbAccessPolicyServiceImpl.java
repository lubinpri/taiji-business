package com.dbms.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dbms.permission.dto.AccessPolicyDTO;
import com.dbms.permission.entity.DbAccessPolicy;
import com.dbms.permission.mapper.DbAccessPolicyMapper;
import com.dbms.permission.service.DbAccessPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据库访问策略Service实现
 */
@Service
@RequiredArgsConstructor
public class DbAccessPolicyServiceImpl extends ServiceImpl<DbAccessPolicyMapper, DbAccessPolicy> implements DbAccessPolicyService {
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createPolicy(AccessPolicyDTO policyDTO) {
        DbAccessPolicy policy = new DbAccessPolicy();
        BeanUtils.copyProperties(policyDTO, policy);
        
        // 检查是否已存在相同的策略（用户+实例+库+表的组合）
        LambdaQueryWrapper<DbAccessPolicy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DbAccessPolicy::getUserId, policyDTO.getUserId())
               .eq(DbAccessPolicy::getInstanceId, policyDTO.getInstanceId())
               .eq(policyDTO.getCatalogName() != null, DbAccessPolicy::getCatalogName, policyDTO.getCatalogName())
               .eq(policyDTO.getTableName() != null, DbAccessPolicy::getTableName, policyDTO.getTableName());
        
        DbAccessPolicy existing = baseMapper.selectOne(wrapper);
        if (existing != null) {
            throw new RuntimeException("该访问策略已存在");
        }
        
        return this.save(policy);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePolicy(AccessPolicyDTO policyDTO) {
        if (policyDTO.getId() == null) {
            throw new RuntimeException("策略ID不能为空");
        }
        
        DbAccessPolicy policy = baseMapper.selectById(policyDTO.getId());
        if (policy == null) {
            throw new RuntimeException("策略不存在");
        }
        
        BeanUtils.copyProperties(policyDTO, policy, "id", "createdBy", "createdTime");
        
        return this.updateById(policy);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePolicy(Long id) {
        return this.removeById(id);
    }
    
    @Override
    public AccessPolicyDTO getPolicyById(Long id) {
        DbAccessPolicy policy = baseMapper.selectById(id);
        if (policy == null) {
            return null;
        }
        return convertToDTO(policy);
    }
    
    @Override
    public List<AccessPolicyDTO> listPoliciesByUserId(Long userId) {
        List<DbAccessPolicy> policies = baseMapper.selectByUserId(userId);
        return policies.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<AccessPolicyDTO> listPoliciesByUserAndInstance(Long userId, Long instanceId) {
        List<DbAccessPolicy> policies = baseMapper.selectByUserIdAndInstanceId(userId, instanceId);
        return policies.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean checkAccess(Long userId, Long instanceId, String catalogName, String tableName, String requiredAccessType) {
        // 查询用户在该实例上的所有策略
        List<DbAccessPolicy> policies = baseMapper.selectByUserIdAndInstanceId(userId, instanceId);
        
        if (policies.isEmpty()) {
            return false;
        }
        
        // 优先级：表 > 库 > 实例
        // 查找最匹配的策略
        DbAccessPolicy matchedPolicy = null;
        int matchLevel = 0; // 0:无匹配, 1:实例, 2:实例+库, 3:实例+库+表
        
        for (DbAccessPolicy policy : policies) {
            // 实例匹配
            if (policy.getInstanceId().equals(instanceId)) {
                // 检查是否有更细粒度的匹配
                if (matchLevel < 1) {
                    matchedPolicy = policy;
                    matchLevel = 1;
                }
                
                // 库匹配
                if (catalogName != null && catalogName.equals(policy.getCatalogName())) {
                    if (matchLevel < 2) {
                        matchedPolicy = policy;
                        matchLevel = 2;
                    }
                    
                    // 表匹配
                    if (tableName != null && tableName.equals(policy.getTableName())) {
                        matchedPolicy = policy;
                        matchLevel = 3;
                        break;
                    }
                }
            }
        }
        
        if (matchedPolicy == null) {
            return false;
        }
        
        // 检查访问类型
        String accessType = matchedPolicy.getAccessType();
        if ("admin".equals(accessType)) {
            return true;
        }
        if ("write".equals(accessType)) {
            return !"admin".equals(requiredAccessType);
        }
        if ("read".equals(accessType)) {
            return "read".equals(requiredAccessType);
        }
        
        return false;
    }
    
    @Override
    public String getMaskLevel(Long userId, Long instanceId, String catalogName, String tableName) {
        // 查询用户在该实例上的所有策略
        List<DbAccessPolicy> policies = baseMapper.selectByUserIdAndInstanceId(userId, instanceId);
        
        if (policies.isEmpty()) {
            return "none";
        }
        
        // 查找最匹配的策略
        String maskLevel = "none";
        int matchLevel = 0;
        
        for (DbAccessPolicy policy : policies) {
            if (policy.getInstanceId().equals(instanceId)) {
                if (matchLevel < 1) {
                    maskLevel = policy.getMaskLevel();
                    matchLevel = 1;
                }
                
                if (catalogName != null && catalogName.equals(policy.getCatalogName())) {
                    if (matchLevel < 2) {
                        maskLevel = policy.getMaskLevel();
                        matchLevel = 2;
                    }
                    
                    if (tableName != null && tableName.equals(policy.getTableName())) {
                        maskLevel = policy.getMaskLevel();
                        matchLevel = 3;
                        break;
                    }
                }
            }
        }
        
        return maskLevel != null ? maskLevel : "none";
    }
    
    private AccessPolicyDTO convertToDTO(DbAccessPolicy policy) {
        AccessPolicyDTO dto = new AccessPolicyDTO();
        BeanUtils.copyProperties(policy, dto);
        return dto;
    }
}
