package com.dbms.backup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dbms.backup.entity.BackupPolicy;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 备份策略Mapper
 */
@Mapper
public interface BackupPolicyMapper extends BaseMapper<BackupPolicy> {
    
    /**
     * 查询启用的策略列表
     */
    @Select("SELECT * FROM backup_policy WHERE enabled = 1 AND deleted = 0")
    List<BackupPolicy> findEnabledPolicies();
    
    /**
     * 根据实例ID查询策略
     */
    @Select("SELECT * FROM backup_policy WHERE instance_id = #{instanceId} AND deleted = 0")
    List<BackupPolicy> findByInstanceId(@Param("instanceId") Long instanceId);
}
