package com.dbms.audit.controller;

import com.dbms.audit.service.QueryLogService;
import com.dbms.common.dto.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 审计服务控制器
 */
@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {
    
    private final QueryLogService queryLogService;
    
    /**
     * 获取查询日志列表
     */
    @GetMapping("/query-logs")
    public Result<List<Map<String, Object>>> getQueryLogs(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long instanceId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime
    ) {
        Map<String, Object> params = new java.util.HashMap<>();
        if (userId != null) params.put("userId", userId);
        if (instanceId != null) params.put("instanceId", instanceId);
        if (startTime != null) params.put("startTime", startTime);
        if (endTime != null) params.put("endTime", endTime);
        
        return Result.success(queryLogService.getQueryLogs(params));
    }
    
    /**
     * 获取审计统计
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime
    ) {
        Map<String, Object> params = new java.util.HashMap<>();
        if (startTime != null) params.put("startTime", startTime);
        if (endTime != null) params.put("endTime", endTime);
        
        return Result.success(queryLogService.getAuditStatistics(params));
    }
}
