package com.dbms.query.service.impl;

import cn.hutool.core.util.StrUtil;
import com.dbms.common.dto.Result;
import com.dbms.common.entity.DbInstance;
import com.dbms.query.dto.*;
import com.dbms.query.feign.MaskClient;
import com.dbms.query.feign.PermissionClient;
import com.dbms.query.feign.WatermarkClient;
import com.dbms.query.service.QueryService;
import com.dbms.query.util.SqlParserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 查询服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QueryServiceImpl implements QueryService {
    
    private final PermissionClient permissionClient;
    private final MaskClient maskClient;
    private final WatermarkClient watermarkClient;
    private final SqlParserUtil sqlParserUtil;
    
    // 简单的数据源缓存(实际项目中应该从dbms-resource获取)
    private static final Map<Long, DataSource> DATA_SOURCE_CACHE = new ConcurrentHashMap<>();
    
    @Override
    public QueryResult executeQuery(QueryExecuteRequest request, Long userId, String userName) {
        long startTime = System.currentTimeMillis();
        
        // 1. 获取SQL类型
        String sqlType = sqlParserUtil.getSqlType(request.getSql());
        
        // 2. 解析SQL获取涉及的表
        List<String> tables = sqlParserUtil.parseTablesFromSql(request.getSql());
        String table = tables.isEmpty() ? null : tables.get(0);
        
        // 3. 调用权限服务进行校验
        Result<Boolean> permResult = permissionClient.checkQueryPermission(
                userId, 
                request.getInstanceId(), 
                request.getDatabase(), 
                table, 
                sqlType
        );
        
        if (permResult == null || permResult.getCode() != 200 || !Boolean.TRUE.equals(permResult.getData())) {
            throw new SecurityException("权限校验失败: " + (permResult != null ? permResult.getMessage() : "服务不可用"));
        }
        
        // 4. 执行SQL
        QueryResult result = executeSql(request, sqlType);
        result.setSqlType(sqlType);
        
        // 5. 如果是SELECT查询，进行脱敏和水印处理
        if ("SELECT".equals(sqlType) && result.getRows() != null && !result.getRows().isEmpty()) {
            // 5.1 调用脱敏服务处理结果
            try {
                Result<List<Map<String, Object>>> maskResult = maskClient.maskData(
                        result.getRows(), 
                        request.getDatabase(), 
                        table != null ? table : ""
                );
                if (maskResult != null && maskResult.getCode() == 200 && maskResult.getData() != null) {
                    result.setRows(maskResult.getData());
                }
            } catch (Exception e) {
                log.warn("脱敏服务调用失败: {}", e.getMessage());
            }
            
            // 5.2 调用水印服务添加水印
            try {
                Result<List<Map<String, Object>>> watermarkResult = watermarkClient.addWatermark(
                        result.getRows(), 
                        userId, 
                        userName != null ? userName : "unknown"
                );
                if (watermarkResult != null && watermarkResult.getCode() == 200 && watermarkResult.getData() != null) {
                    result.setRows(watermarkResult.getData());
                }
            } catch (Exception e) {
                log.warn("水印服务调用失败: {}", e.getMessage());
            }
        }
        
        result.setExecutionTime(System.currentTimeMillis() - startTime);
        
        return result;
    }
    
    @Override
    public List<TableInfo> getTableList(Long instanceId, String database, Long userId) {
        // 权限校验 - 检查对数据库的访问权限
        Result<Boolean> permResult = permissionClient.checkQueryPermission(
                userId, instanceId, database, null, "SELECT"
        );
        
        if (permResult == null || permResult.getCode() != 200 || !Boolean.TRUE.equals(permResult.getData())) {
            throw new SecurityException("权限校验失败");
        }
        
        List<TableInfo> tables = new ArrayList<>();
        
        String sql = "SELECT TABLE_NAME, TABLE_COMMENT, ENGINE, TABLE_ROWS, " +
                     "DATA_LENGTH, INDEX_LENGTH, CREATE_TIME " +
                     "FROM information_schema.TABLES " +
                     "WHERE TABLE_SCHEMA = ? AND TABLE_TYPE = 'BASE TABLE'";
        
        try (Connection conn = getConnection(instanceId, database);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, database);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                TableInfo info = TableInfo.builder()
                        .tableName(rs.getString("TABLE_NAME"))
                        .comment(rs.getString("TABLE_COMMENT"))
                        .engine(rs.getString("ENGINE"))
                        .rowCount(rs.getLong("TABLE_ROWS"))
                        .dataLength(rs.getLong("DATA_LENGTH"))
                        .indexLength(rs.getLong("INDEX_LENGTH"))
                        .createTime(rs.getString("CREATE_TIME"))
                        .build();
                tables.add(info);
            }
            
        } catch (SQLException e) {
            log.error("获取表列表失败", e);
            throw new RuntimeException("获取表列表失败: " + e.getMessage());
        }
        
        return tables;
    }
    
    @Override
    public List<ColumnInfo> getTableColumns(Long instanceId, String database, String tableName, Long userId) {
        // 权限校验
        Result<Boolean> permResult = permissionClient.checkQueryPermission(
                userId, instanceId, database, tableName, "SELECT"
        );
        
        if (permResult == null || permResult.getCode() != 200 || !Boolean.TRUE.equals(permResult.getData())) {
            throw new SecurityException("权限校验失败");
        }
        
        List<ColumnInfo> columns = new ArrayList<>();
        
        String sql = "SELECT COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE, COLUMN_DEFAULT, " +
                     "COLUMN_KEY, COLUMN_COMMENT, CHARACTER_SET_NAME, COLUMN_COMMENT, " +
                     "NUMERIC_PRECISION, NUMERIC_SCALE " +
                     "FROM information_schema.COLUMNS " +
                     "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? " +
                     "ORDER BY ORDINAL_POSITION";
        
        try (Connection conn = getConnection(instanceId, database);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, database);
            ps.setString(2, tableName);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                ColumnInfo info = ColumnInfo.builder()
                        .name(rs.getString("COLUMN_NAME"))
                        .type(rs.getString("COLUMN_TYPE"))
                        .nullable("YES".equals(rs.getString("IS_NULLABLE")))
                        .defaultValue(rs.getString("COLUMN_DEFAULT"))
                        .keyType(rs.getString("COLUMN_KEY"))
                        .comment(rs.getString("COLUMN_COMMENT"))
                        .charset(rs.getString("CHARACTER_SET_NAME"))
                        .decimalDigits(rs.getInt("NUMERIC_SCALE"))
                        .build();
                
                // 解析字段长度
                String columnType = info.getType();
                if (columnType != null && columnType.contains("(")) {
                    try {
                        String lengthStr = columnType.substring(columnType.indexOf("(") + 1, columnType.indexOf(")"));
                        info.setLength(Integer.parseInt(lengthStr.split(",")[0].trim()));
                    } catch (Exception ignored) {}
                }
                
                columns.add(info);
            }
            
        } catch (SQLException e) {
            log.error("获取表字段失败", e);
            throw new RuntimeException("获取表字段失败: " + e.getMessage());
        }
        
        return columns;
    }
    
    @Override
    public QueryResult buildAndExecuteQuery(QueryVisualizeRequest request, Long userId, String userName) {
        // 构建SQL
        String sql = sqlParserUtil.buildVisualizeQuerySql(request);
        
        // 创建查询请求
        QueryExecuteRequest queryRequest = new QueryExecuteRequest();
        queryRequest.setInstanceId(request.getInstanceId());
        queryRequest.setDatabase(request.getDatabase());
        queryRequest.setSql(sql);
        queryRequest.setPageNum(request.getPageNum());
        queryRequest.setPageSize(request.getPageSize());
        
        // 执行查询
        return executeQuery(queryRequest, userId, userName);
    }
    
    @Override
    public List<String> parseTablesFromSql(String sql) {
        return sqlParserUtil.parseTablesFromSql(sql);
    }
    
    @Override
    public String getSqlType(String sql) {
        return sqlParserUtil.getSqlType(sql);
    }
    
    /**
     * 执行SQL语句
     */
    private QueryResult executeSql(QueryExecuteRequest request, String sqlType) {
        QueryResult result = new QueryResult();
        
        try (Connection conn = getConnection(request.getInstanceId(), request.getDatabase())) {
            
            // 处理分页
            String sql = request.getSql();
            if ("SELECT".equals(sqlType) && request.getPageSize() != null && request.getPageSize() > 0) {
                if (!sql.toLowerCase().contains("limit")) {
                    int offset = (request.getPageNum() - 1) * request.getPageSize();
                    sql = sql.trim();
                    if (sql.endsWith(";")) {
                        sql = sql.substring(0, sql.length() - 1);
                    }
                    sql += " LIMIT " + offset + ", " + request.getPageSize();
                }
            }
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                // 获取列信息
                ResultSetMetaData metaData = rs.getMetaData();
                List<ColumnInfo> columns = new ArrayList<>();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    ColumnInfo col = ColumnInfo.builder()
                            .name(metaData.getColumnName(i))
                            .type(metaData.getColumnTypeName(i))
                            .nullable(metaData.isNullable(i) == ResultSetMetaData.columnNullable)
                            .build();
                    columns.add(col);
                }
                result.setColumns(columns);
                
                // 获取数据
                List<Map<String, Object>> rows = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        row.put(metaData.getColumnName(i), rs.getObject(i));
                    }
                    rows.add(row);
                }
                result.setRows(rows);
                result.setTotal((long) rows.size());
                
                // 计算总数(如果需要)
                if ("SELECT".equals(sqlType) && request.getPageSize() != null) {
                    result.setPageNum(request.getPageNum());
                    result.setPageSize(request.getPageSize());
                }
            }
            
        } catch (SQLException e) {
            log.error("SQL执行失败: {}", e.getMessage());
            throw new RuntimeException("SQL执行失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 获取数据库连接
     * 实际项目中应该从dbms-resource模块获取
     */
    private Connection getConnection(Long instanceId, String database) {
        // 这里应该调用dbms-resource服务获取数据源配置
        // 为了简化，直接使用本地配置
        try {
            String url = "jdbc:mysql://localhost:3306/" + database + 
                        "?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
            return DriverManager.getConnection(url, "root", "root");
        } catch (SQLException e) {
            throw new RuntimeException("获取数据库连接失败: " + e.getMessage());
        }
    }
}
