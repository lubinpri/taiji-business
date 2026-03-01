package com.dbms.backup.scheduler;

import com.dbms.backup.entity.BackupPolicy;
import com.dbms.backup.service.BackupPolicyService;
import com.dbms.backup.service.BackupTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 备份调度器
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "backup.schedule.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class BackupScheduler {
    
    private final BackupPolicyService backupPolicyService;
    private final BackupTaskService backupTaskService;
    
    /**
     * 定时检查并执行备份任务
     * 每分钟执行一次
     */
    @Scheduled(cron = "0 * * * * ?")
    public void executeScheduledBackup() {
        log.debug("开始检查定时备份任务...");
        
        List<BackupPolicy> enabledPolicies = backupPolicyService.getEnabledPolicies();
        
        for (BackupPolicy policy : enabledPolicies) {
            if (isTimeToBackup(policy)) {
                log.info("触发定时备份: policyId={}, policyName={}", 
                        policy.getId(), policy.getPolicyName());
                
                try {
                    backupTaskService.executeBackup(policy);
                } catch (Exception e) {
                    log.error("定时备份执行失败: policyId={}, error={}", 
                            policy.getId(), e.getMessage());
                }
            }
        }
    }
    
    /**
     * 清理过期备份
     * 每天凌晨3点执行
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanExpiredBackups() {
        log.info("开始清理过期备份...");
        
        try {
            backupTaskService.cleanExpiredBackups();
            log.info("清理过期备份完成");
        } catch (Exception e) {
            log.error("清理过期备份失败: error={}", e.getMessage());
        }
    }
    
    /**
     * 判断是否应该执行备份
     * 简单实现：检查是否有cron表达式，或者简单按策略执行
     */
    private boolean isTimeToBackup(BackupPolicy policy) {
        // 如果没有配置cron，则默认不执行定时任务
        if (policy.getScheduleCron() == null || policy.getScheduleCron().isEmpty()) {
            return false;
        }
        
        // TODO: 实现更复杂的cron解析和判断
        // 简单实现：每次都执行（实际生产应该解析cron表达式判断是否匹配当前时间）
        
        return true;
    }
}
