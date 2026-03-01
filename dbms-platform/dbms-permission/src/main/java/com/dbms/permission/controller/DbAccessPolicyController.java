package com.dbms.permission.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dbms.permission.dto.AccessPolicyDTO;
import com.dbms.permission.dto.Result;
import com.dbms.permission.entity.DbAccessPolicy;
import com.dbms.permission.service.DbAccessPolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 数据库访问策略Controller
 */
@RestController
@RequestMapping("/api/access-policies")
@RequiredArgsConstructor
public class DbAccessPolicyController {
    
    private final DbAccessPolicyService dbAccessPolicyService;
    
    /**
     * 分页查询访问策略列表
     */
    @GetMapping
    public Result<Page<DbAccessPolicy>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long instanceId,
            @RequestParam(required = false) String catalogName,
            @RequestParam(required = false) String accessType) {
        
        Page<DbAccessPolicy> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<DbAccessPolicy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(userId != null, DbAccessPolicy::getUserId, userId)
               .eq(instanceId != null, DbAccessPolicy::getInstanceId, instanceId)
               .like(catalogName != null, DbAccessPolicy::getCatalogName, catalogName)
               .eq(accessType != null, DbAccessPolicy::getAccessType, accessType)
               .orderByDesc(DbAccessPolicy::getCreatedTime);
        
        return Result.success(dbAccessPolicyService.page(page, wrapper));
    }
    
    /**
     * 根据ID查询访问策略
     */
    @GetMapping("/{id}")
    public Result<AccessPolicyDTO> getById(@PathVariable Long id) {
        AccessPolicyDTO policy = dbAccessPolicyService.getPolicyById(id);
        if (policy == null) {
            return Result.error("策略不存在");
        }
        return Result.success(policy);
    }
    
    /**
     * 查询用户的所有访问策略
     */
    @GetMapping("/user/{userId}")
    public Result<List<AccessPolicyDTO>> listByUserId(@PathVariable Long userId) {
        List<AccessPolicyDTO> policies = dbAccessPolicyService.listPoliciesByUserId(userId);
        return Result.success(policies);
    }
    
    /**
     * 查询用户在指定实例上的访问策略
     */
    @GetMapping("/user/{userId}/instance/{instanceId}")
    public Result<List<AccessPolicyDTO>> listByUserAndInstance(
            @PathVariable Long userId,
            @PathVariable Long instanceId) {
        List<AccessPolicyDTO> policies = dbAccessPolicyService.listPoliciesByUserAndInstance(userId, instanceId);
        return Result.success(policies);
    }
    
    /**
     * 创建访问策略
     */
    @PostMapping
    public Result<Void> create(@Valid @RequestBody AccessPolicyDTO policyDTO) {
        try {
            dbAccessPolicyService.createPolicy(policyDTO);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 批量创建访问策略
     */
    @PostMapping("/batch")
    public Result<Void> createBatch(@RequestBody List<AccessPolicyDTO> policyDTOs) {
        try {
            for (AccessPolicyDTO policyDTO : policyDTOs) {
                dbAccessPolicyService.createPolicy(policyDTO);
            }
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新访问策略
     */
    @PutMapping
    public Result<Void> update(@Valid @RequestBody AccessPolicyDTO policyDTO) {
        try {
            dbAccessPolicyService.updatePolicy(policyDTO);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除访问策略
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dbAccessPolicyService.deletePolicy(id);
        return Result.success();
    }
    
    /**
     * 校验用户访问权限
     */
    @GetMapping("/check")
    public Result<Boolean> checkAccess(
            @RequestParam Long userId,
            @RequestParam Long instanceId,
            @RequestParam(required = false) String catalogName,
            @RequestParam(required = false) String tableName,
            @RequestParam(defaultValue = "read") String accessType) {
        
        boolean hasAccess = dbAccessPolicyService.checkAccess(userId, instanceId, catalogName, tableName, accessType);
        return Result.success(hasAccess);
    }
    
    /**
     * 获取脱敏级别
     */
    @GetMapping("/mask-level")
    public Result<String> getMaskLevel(
            @RequestParam Long userId,
            @RequestParam Long instanceId,
            @RequestParam(required = false) String catalogName,
            @RequestParam(required = false) String tableName) {
        
        String maskLevel = dbAccessPolicyService.getMaskLevel(userId, instanceId, catalogName, tableName);
        return Result.success(maskLevel);
    }
}
