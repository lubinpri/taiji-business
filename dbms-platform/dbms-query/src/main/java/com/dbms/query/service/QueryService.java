package com.dbms.query.service;

import com.dbms.query.dto.*;

import java.util.List;
import java.util.Map;

/**
 * 查询服务接口
 */
public interface QueryService {
    
    /**
     * 执行SQL查询
     * @param request 查询请求
     * @param userId 用户ID
     * @param userName 用户名
     * @return 查询结果
     */
    QueryResult executeQuery(QueryExecuteRequest request, Long userId, String userName);
    
    /**
     * 获取数据库表列表
     * @param instanceId 实例ID
     * @param database 数据库名
     * @param userId 用户ID
     * @return 表信息列表
     */
    List<TableInfo> getTableList(Long instanceId, String database, Long userId);
    
    /**
     * 获取表字段信息
     * @param instanceId 实例ID
     * @param database 数据库名
     * @param tableName 表名
     * @param userId 用户ID
     * @return 列信息列表
     */
    List<ColumnInfo> getTableColumns(Long instanceId, String database, String tableName, Long userId);
    
    /**
     * 可视化查询构建
     * @param request 可视化查询请求
     * @param userId 用户ID
     * @param userName 用户名
     * @return 查询结果
     */
    QueryResult buildAndExecuteQuery(QueryVisualizeRequest request, Long userId, String userName);
    
    /**
     * 解析SQL语句获取涉及到的表
     * @param sql SQL语句
     * @return 表名列表
     */
    List<String> parseTablesFromSql(String sql);
    
    /**
     * 获取SQL类型
     * @param sql SQL语句
     * @return SQL类型 (SELECT, INSERT, UPDATE, DELETE, OTHER)
     */
    String getSqlType(String sql);
}
