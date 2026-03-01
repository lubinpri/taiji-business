package com.dbms.audit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dbms.audit.mapper.QueryLogMapper;
import com.dbms.audit.service.QueryLogService;
import com.dbms.common.entity.QueryLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class QueryLogServiceImpl extends ServiceImpl<QueryLogMapper, QueryLog> implements QueryLogService {
    
    @Override
    public void logQuery(Long userId, Long instanceId, String sql, Integer queryTime,
                         Integer resultRows, String ipAddress, String watermarkInfo) {
        QueryLog queryLog = new QueryLog();
        queryLog.setUserId(userId);
        queryLog.setInstanceId(instanceId);
        queryLog.setSqlText(sql);
        queryLog.setQueryTime(queryTime);
        queryLog.setResultRows(resultRows);
        queryLog.setIpAddress(ipAddress);
        queryLog.setWatermarkInfo(watermarkInfo);
        
        this.save(queryLog);
        log.info("记录查询日志: userId={}, instanceId={}, sql={}", userId, instanceId, sql);
    }
    
    @Override
    public List<Map<String, Object>> getQueryLogs(Map<String, Object> params) {
        LambdaQueryWrapper<QueryLog> wrapper = new LambdaQueryWrapper<>();
        
        if (params.containsKey("userId")) {
            wrapper.eq(QueryLog::getUserId, params.get("userId"));
        }
        if (params.containsKey("instanceId")) {
            wrapper.eq(QueryLog::getInstanceId, params.get("instanceId"));
        }
        if (params.containsKey("startTime")) {
            wrapper.ge(QueryLog::getCreatedTime, params.get("startTime"));
        }
        if (params.containsKey("endTime")) {
            wrapper.le(QueryLog::getCreatedTime, params.get("endTime"));
        }
        
        wrapper.orderByDesc(QueryLog::getCreatedTime);
        
        List<QueryLog> logs = this.list(wrapper);
        
        return logs.stream().map(log -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", log.getId());
            map.put("userId", log.getUserId());
            map.put("instanceId", log.getInstanceId());
            map.put("sqlText", log.getSqlText());
            map.put("queryTime", log.getQueryTime());
            map.put("resultRows", log.getResultRows());
            map.put("ipAddress", log.getIpAddress());
            map.put("watermarkInfo", log.getWatermarkInfo());
            map.put("createdTime", log.getCreatedTime());
            return map;
        }).toList();
    }
    
    @Override
    public Map<String, Object> getAuditStatistics(Map<String, Object> params) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 统计查询总数
        LambdaQueryWrapper<QueryLog> wrapper = new LambdaQueryWrapper<>();
        if (params.containsKey("startTime")) {
            wrapper.ge(QueryLog::getCreatedTime, params.get("startTime"));
        }
        if (params.containsKey("endTime")) {
            wrapper.le(QueryLog::getCreatedTime, params.get("endTime"));
        }
        Long totalQueries = this.count(wrapper);
        statistics.put("totalQueries", totalQueries);
        
        // 统计平均查询时间
        List<QueryLog> logs = this.list(wrapper);
        if (!logs.isEmpty()) {
            double avgQueryTime = logs.stream()
                    .mapToInt(QueryLog::getQueryTime)
                    .average()
                    .orElse(0);
            statistics.put("avgQueryTime", avgQueryTime);
        }
        
        return statistics;
    }
}
