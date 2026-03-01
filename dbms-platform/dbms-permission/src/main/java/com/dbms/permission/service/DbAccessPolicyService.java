package com.dbms.permission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dbms.permission.dto.AccessPolicyDTO;
import com.dbms.permission.entity.DbAccessPolicy;
import java.util.List;

/**
 * 数据库访问策略Service接口
 */
public interface DbAccessPolicyService extends IService<DbAccessPolicy> {
    
    /**
     * 创建访问策略
     */
    boolean createPolicy(AccessPolicyDTO policyDTO);
    
    /**
     * 更新访问策略
     */
    boolean updatePolicy(AccessPolicyDTO policyDTO);
    
    /**
     * 删除访问策略
     */
    boolean deletePolicy(Long id);
    
    /**
     * 根据ID查询访问策略
     */
    AccessPolicyDTO getPolicyById(Long id);
    
    /**
     * 查询用户的所有访问策略
     */
    List<AccessPolicyDTO> listPoliciesByUserId(Long userId);
    
    /**
     * 查询用户在指定实例上的访问策略
     */
    List<AccessPolicyDTO> listPoliciesByUserAndInstance(Long userId, Long instanceId);
    
    /**
     * 校验用户对指定数据库的访问权限
     */
    boolean checkAccess(Long userId, Long instanceId, String catalogName, String tableName, String requiredAccessType);
    
    /**
     * 获取用户的脱敏级别
     */
    String getMaskLevel(Long userId, Long instanceId, String catalogName, String tableName);
}
