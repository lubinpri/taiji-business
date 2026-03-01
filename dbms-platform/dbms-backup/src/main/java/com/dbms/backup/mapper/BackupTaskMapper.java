package com.dbms.backup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dbms.backup.dto.BackupTaskDTO;
import com.dbms.backup.entity.BackupTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 备份任务Mapper
 */
@Mapper
public interface BackupTaskMapper extends BaseMapper<BackupTask> {
    
    /**
     * 查询任务详情（含策略信息）
     */
    @Select("""
        SELECT t.*, p.policy_name, p.backup_type, p.schedule_cron, p.retain_days,
               i.instance_name
        FROM backup_task t
        LEFT JOIN backup_policy p ON t.policy_id = p.id
        LEFT JOIN db_instance i ON p.instance_id = i.id
        WHERE t.id = #{taskId} AND t.deleted = 0
        """)
    BackupTaskDTO getTaskDetail(@Param("taskId") Long taskId);
    
    /**
     * 分页查询任务列表（含策略信息）
     */
    @Select("""
        SELECT t.*, p.policy_name, p.backup_type, p.schedule_cron, p.retain_days,
               i.instance_name
        FROM backup_task t
        LEFT JOIN backup_policy p ON t.policy_id = p.id
        LEFT JOIN db_instance i ON p.instance_id = i.id
        WHERE t.deleted = 0
        ORDER BY t.created_time DESC
        """)
    IPage<BackupTaskDTO> pageTask(Page<BackupTaskDTO> page);
    
    /**
     * 根据策略ID查询最近成功的任务
     */
    @Select("""
        SELECT * FROM backup_task 
        WHERE policy_id = #{policyId} AND task_status = 'success' 
        AND deleted = 0 
        ORDER BY end_time DESC 
        LIMIT 1
        """)
    BackupTask findLatestSuccessByPolicyId(@Param("policyId") Long policyId);
    
    /**
     * 统计各状态任务数量
     */
    @Select("""
        SELECT task_status, COUNT(*) as count 
        FROM backup_task 
        WHERE deleted = 0 
        GROUP BY task_status
        """)
    List<StatusCount> countByStatus();
    
    /**
     * 状态统计内部类
     */
    class StatusCount {
        private String taskStatus;
        private Long count;
        
        public String getTaskStatus() { return taskStatus; }
        public void setTaskStatus(String taskStatus) { this.taskStatus = taskStatus; }
        public Long getCount() { return count; }
        public void setCount(Long count) { this.count = count; }
    }
}
