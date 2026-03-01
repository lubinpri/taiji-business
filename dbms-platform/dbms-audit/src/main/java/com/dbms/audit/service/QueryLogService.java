package com.dbms.audit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dbms.common.entity.QueryLog;

import java.util.List;
import java.util.Map;

public interface QueryLogService extends IService<QueryLog> {
    
    /**
     * 记录查询日志
     */
    void logQuery(Long userId, Long instanceId, String sql, Integer queryTime, 
                  Integer resultRows, String ipAddress, String watermarkInfo);
    
    /**
     * 获取查询日志列表
     */
    List<Map<String, Object>> getQueryLogs(Map<String, Object> params);
    
    /**
     * 获取审计统计
     */
    Map<String, Object> getAuditStatistics(Map<String, Object> params);
}
