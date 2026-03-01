package com.dbms.common.util;

import cn.hutool.core.util.StrUtil;
import com.dbms.common.entity.DbInstance;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库连接器工厂
 */
@Slf4j
public class DatabaseConnector {
    
    private final DbInstance instance;
    private Connection connection;
    
    public DatabaseConnector(DbInstance instance) {
        this.instance = instance;
    }
    
    /**
     * 获取连接
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName(getDriverClass());
                String url = buildJdbcUrl();
                connection = DriverManager.getConnection(url, instance.getUsername(), instance.getPassword());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("数据库驱动加载失败: " + e.getMessage(), e);
            }
        }
        return connection;
    }
    
    /**
     * 获取JDBC驱动类
     */
    private String getDriverClass() {
        return switch (instance.getInstanceType().toUpperCase()) {
            case "MYSQL" -> "com.mysql.cj.jdbc.Driver";
            case "ORACLE" -> "oracle.jdbc.OracleDriver";
            case "POSTGRESQL" -> "org.postgresql.Driver";
            case "DM" -> "dm.jdbc.driver.DmDriver";
            case "KINGBASE" -> "com.kingbase8.Driver";
            default -> throw new RuntimeException("不支持的数据库类型: " + instance.getInstanceType());
        };
    }
    
    /**
     * 构建JDBC URL
     */
    private String buildJdbcUrl() {
        String type = instance.getInstanceType().toUpperCase();
        return switch (type) {
            case "MYSQL" -> String.format("jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai",
                    instance.getHost(), instance.getPort(), instance.getServiceName());
            case "ORACLE" -> String.format("jdbc:oracle:thin:@%s:%d:%s",
                    instance.getHost(), instance.getPort(), instance.getServiceName());
            case "POSTGRESQL" -> String.format("jdbc:postgresql://%s:%d/%s",
                    instance.getHost(), instance.getPort(), instance.getServiceName());
            case "DM" -> String.format("jdbc:dm://%s:%d",
                    instance.getHost(), instance.getPort());
            case "KINGBASE" -> String.format("jdbc:kingbase8://%s:%d/%s",
                    instance.getHost(), instance.getPort(), instance.getServiceName());
            default -> throw new RuntimeException("不支持的数据库类型: " + type);
        };
    }
    
    /**
     * 测试连接
     */
    public boolean testConnection() {
        try {
            getConnection();
            return true;
        } catch (Exception e) {
            log.error("数据库连接测试失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 获取数据库列表
     */
    public List<String> getCatalogs() throws SQLException {
        List<String> catalogs = new ArrayList<>();
        DatabaseMetaData metaData = getConnection().getMetaData();
        try (ResultSet rs = metaData.getCatalogs()) {
            while (rs.next()) {
                catalogs.add(rs.getString("TABLE_CAT"));
            }
        }
        return catalogs;
    }
    
    /**
     * 获取表列表
     */
    public List<String> getTables(String catalog) throws SQLException {
        List<String> tables = new ArrayList<>();
        DatabaseMetaData metaData = getConnection().getMetaData();
        try (ResultSet rs = metaData.getTables(catalog, null, "%", new String[]{"TABLE", "VIEW"})) {
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
        }
        return tables;
    }
    
    /**
     * 获取字段信息
     */
    public List<Map<String, String>> getColumns(String catalog, String table) throws SQLException {
        List<Map<String, String>> columns = new ArrayList<>();
        DatabaseMetaData metaData = getConnection().getMetaData();
        try (ResultSet rs = metaData.getColumns(catalog, null, table, "%")) {
            while (rs.next()) {
                Map<String, String> col = new HashMap<>();
                col.put("name", rs.getString("COLUMN_NAME"));
                col.put("type", rs.getString("TYPE_NAME"));
                col.put("size", rs.getString("COLUMN_SIZE"));
                col.put("nullable", rs.getString("IS_NULLABLE"));
                col.put("remark", rs.getString("REMARKS"));
                columns.add(col);
            }
        }
        return columns;
    }
    
    /**
     * 执行查询
     */
    public List<Map<String, Object>> executeQuery(String sql) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnLabel(i), rs.getObject(i));
                }
                results.add(row);
            }
        }
        return results;
    }
    
    /**
     * 关闭连接
     */
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                log.error("关闭数据库连接失败: {}", e.getMessage());
            }
        }
    }
}
