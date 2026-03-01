package com.dbms.backup.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dbms.backup.dto.BackupPolicyRequest;
import com.dbms.backup.dto.BackupTaskDTO;
import com.dbms.backup.dto.RestoreRequest;
import com.dbms.backup.entity.BackupPolicy;
import com.dbms.backup.service.BackupPolicyService;
import com.dbms.backup.service.BackupTaskService;
import com.dbms.common.dto.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 备份管理控制器
 */
@RestController
@RequestMapping("/api/backup")
@RequiredArgsConstructor
public class BackupController {
    
    private final BackupPolicyService backupPolicyService;
    private final BackupTaskService backupTaskService;
    
    // ==================== 策略管理 ====================
    
    /**
     * 创建备份策略
     */
    @PostMapping("/policies")
    public Result<Boolean> createPolicy(
            @Valid @RequestBody BackupPolicyRequest request,
            @RequestHeader(value = "X-User-Name", defaultValue = "admin") String username) {
        return Result.success(backupPolicyService.createPolicy(request, username));
    }
    
    /**
     * 获取策略列表
     */
    @GetMapping("/policies")
    public Result<Page<BackupPolicy>> listPolicies(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String policyName,
            @RequestParam(required = false) Long instanceId) {
        Page<BackupPolicy> page = new Page<>(pageNum, pageSize);
        return Result.success(backupPolicyService.pagePolicy(page, policyName, instanceId));
    }
    
    /**
     * 获取策略详情
     */
    @GetMapping("/policies/{id}")
    public Result<BackupPolicy> getPolicy(@PathVariable Long id) {
        return Result.success(backupPolicyService.getPolicyDetail(id));
    }
    
    /**
     * 更新策略
     */
    @PutMapping("/policies/{id}")
    public Result<Boolean> updatePolicy(
            @PathVariable Long id,
            @Valid @RequestBody BackupPolicyRequest request,
            @RequestHeader(value = "X-User-Name", defaultValue = "admin") String username) {
        return Result.success(backupPolicyService.updatePolicy(id, request, username));
    }
    
    /**
     * 删除策略
     */
    @DeleteMapping("/policies/{id}")
    public Result<Boolean> deletePolicy(@PathVariable Long id) {
        return Result.success(backupPolicyService.deletePolicy(id));
    }
    
    /**
     * 启用/禁用策略
     */
    @PutMapping("/policies/{id}/toggle")
    public Result<Boolean> togglePolicy(
            @PathVariable Long id,
            @RequestParam Integer enabled) {
        return Result.success(backupPolicyService.togglePolicy(id, enabled));
    }
    
    // ==================== 任务管理 ====================
    
    /**
     * 手动触发备份
     */
    @PostMapping("/tasks/backup")
    public Result<Boolean> manualBackup(@RequestParam Long policyId) {
        return Result.success(backupTaskService.manualBackup(policyId));
    }
    
    /**
     * 获取任务列表
     */
    @GetMapping("/tasks")
    public Result<Page<BackupTaskDTO>> listTasks(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long policyId,
            @RequestParam(required = false) String taskStatus) {
        Page<BackupTaskDTO> page = new Page<>(pageNum, pageSize);
        return Result.success(backupTaskService.pageTask(page, policyId, taskStatus));
    }
    
    /**
     * 获取任务详情
     */
    @GetMapping("/tasks/{id}")
    public Result<BackupTaskDTO> getTask(@PathVariable Long id) {
        return Result.success(backupTaskService.getTaskDetail(id));
    }
    
    /**
     * 取消任务
     */
    @PostMapping("/tasks/{id}/cancel")
    public Result<Boolean> cancelTask(@PathVariable Long id) {
        return Result.success(backupTaskService.cancelTask(id));
    }
    
    /**
     * 执行恢复
     */
    @PostMapping("/tasks/restore")
    public Result<Boolean> restore(@Valid @RequestBody RestoreRequest request) {
        return Result.success(backupTaskService.restore(request));
    }
    
    // ==================== 监控统计 ====================
    
    /**
     * 获取任务统计
     */
    @GetMapping("/statistics")
    public Result<Map<String, Long>> getStatistics() {
        return Result.success(backupTaskService.getTaskStatistics());
    }
    
    /**
     * 获取所有启用的策略
     */
    @GetMapping("/policies/enabled")
    public Result<List<BackupPolicy>> getEnabledPolicies() {
        return Result.success(backupPolicyService.getEnabledPolicies());
    }
}
