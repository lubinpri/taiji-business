package com.dbms.query.controller;

import com.dbms.common.dto.Result;
import com.dbms.query.dto.*;
import com.dbms.query.service.QueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 查询控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/query")
@RequiredArgsConstructor
public class QueryController {
    
    private final QueryService queryService;
    
    /**
     * 执行SQL查询
     */
    @PostMapping("/execute")
    public Result<QueryResult> executeQuery(
            @Validated @RequestBody QueryExecuteRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
            @RequestHeader(value = "X-User-Name", defaultValue = "anonymous") String userName) {
        
        log.info("用户[{}]执行SQL查询: {}", userName, request.getSql());
        
        try {
            QueryResult result = queryService.executeQuery(request, userId, userName);
            return Result.success(result);
        } catch (SecurityException e) {
            log.warn("权限校验失败: {}", e.getMessage());
            return Result.error(403, e.getMessage());
        } catch (Exception e) {
            log.error("查询执行失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取数据库表列表
     */
    @GetMapping("/tables")
    public Result<List<TableInfo>> getTableList(
            @RequestParam Long instanceId,
            @RequestParam String database,
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        
        try {
            List<TableInfo> tables = queryService.getTableList(instanceId, database, userId);
            return Result.success(tables);
        } catch (SecurityException e) {
            return Result.error(403, e.getMessage());
        } catch (Exception e) {
            log.error("获取表列表失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取表字段信息
     */
    @GetMapping("/columns")
    public Result<List<ColumnInfo>> getTableColumns(
            @RequestParam Long instanceId,
            @RequestParam String database,
            @RequestParam String tableName,
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        
        try {
            List<ColumnInfo> columns = queryService.getTableColumns(instanceId, database, tableName, userId);
            return Result.success(columns);
        } catch (SecurityException e) {
            return Result.error(403, e.getMessage());
        } catch (Exception e) {
            log.error("获取表字段失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 可视化查询构建与执行
     */
    @PostMapping("/visualize")
    public Result<QueryResult> visualizeQuery(
            @Validated @RequestBody QueryVisualizeRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
            @RequestHeader(value = "X-User-Name", defaultValue = "anonymous") String userName) {
        
        log.info("用户[{}]执行可视化查询: table={}", userName, request.getTableName());
        
        try {
            QueryResult result = queryService.buildAndExecuteQuery(request, userId, userName);
            return Result.success(result);
        } catch (SecurityException e) {
            log.warn("权限校验失败: {}", e.getMessage());
            return Result.error(403, e.getMessage());
        } catch (Exception e) {
            log.error("可视化查询执行失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 解析SQL语句(预览)
     */
    @PostMapping("/parse")
    public Result<SqlParseResult> parseSql(@RequestBody String sql) {
        try {
            List<String> tables = queryService.parseTablesFromSql(sql);
            String sqlType = queryService.getSqlType(sql);
            
            SqlParseResult result = new SqlParseResult();
            result.setTables(tables);
            result.setSqlType(sqlType);
            result.setReadOnly("SELECT".equals(sqlType) || "SHOW".equals(sqlType));
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("SQL解析失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * SQL解析结果
     */
    @lombok.Data
    public static class SqlParseResult {
        private List<String> tables;
        private String sqlType;
        private Boolean readOnly;
    }
}
