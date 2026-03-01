package com.dbms.backup.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dbms.backup.dto.BackupTaskDTO;
import com.dbms.backup.dto.RestoreRequest;
import com.dbms.backup.entity.BackupPolicy;
import com.dbms.backup.entity.BackupTask;

import java.util.List;
import java.util.Map;

/**
 * 备份任务服务
 */
public interface BackupTaskService extends IService<BackupTask> {
    
    /**
     * 执行备份任务
     */
    BackupTask executeBackup(BackupPolicy policy);
    
    /**
     * 手动触发备份
     */
    boolean manualBackup(Long policyId);
    
    /**
     * 执行恢复
     */
    boolean restore(RestoreRequest request);
    
    /**
     * 获取任务详情
     */
    BackupTaskDTO getTaskDetail(Long taskId);
    
    /**
     * 分页查询任务
     */
    Page<BackupTaskDTO> pageTask(Page<BackupTaskDTO> page, Long policyId, String taskStatus);
    
    /**
     * 获取任务监控统计
     */
    Map<String, Long> getTaskStatistics();
    
    /**
     * 清理过期备份
     */
    void cleanExpiredBackups();
    
    /**
     * 取消任务
     */
    boolean cancelTask(Long taskId);
}
