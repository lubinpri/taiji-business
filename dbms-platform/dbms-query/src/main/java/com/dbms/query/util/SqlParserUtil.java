package com.dbms.query.util;

import cn.hutool.core.util.StrUtil;
import com.dbms.query.dto.QueryVisualizeRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL解析工具类
 */
@Component
public class SqlParserUtil {
    
    // 匹配FROM和JOIN后面的表名
    private static final Pattern TABLE_PATTERN = Pattern.compile(
            "(?:FROM|JOIN|INTO|UPDATE)\\s+`?(\\w+)`?",
            Pattern.CASE_INSENSITIVE
    );
    
    // SQL类型关键词
    private static final String SELECT = "SELECT";
    private static final String INSERT = "INSERT";
    private static final String UPDATE = "UPDATE";
    private static final String DELETE = "DELETE";
    private static final String SHOW = "SHOW";
    private static final String DESCRIBE = "DESCRIBE";
    private static final String EXPLAIN = "EXPLAIN";
    
    /**
     * 解析SQL语句获取涉及到的表
     * @param sql SQL语句
     * @return 表名列表
     */
    public List<String> parseTablesFromSql(String sql) {
        List<String> tables = new ArrayList<>();
        if (StrUtil.isBlank(sql)) {
            return tables;
        }
        
        // 清理SQL中的注释
        String cleanSql = removeComments(sql);
        
        Matcher matcher = TABLE_PATTERN.matcher(cleanSql);
        while (matcher.find()) {
            String table = matcher.group(1);
            if (!tables.contains(table)) {
                tables.add(table);
            }
        }
        
        return tables;
    }
    
    /**
     * 获取SQL类型
     * @param sql SQL语句
     * @return SQL类型
     */
    public String getSqlType(String sql) {
        if (StrUtil.isBlank(sql)) {
            return "OTHER";
        }
        
        String upperSql = sql.trim().toUpperCase();
        
        if (upperSql.startsWith(SELECT)) {
            return "SELECT";
        } else if (upperSql.startsWith(INSERT)) {
            return "INSERT";
        } else if (upperSql.startsWith(UPDATE)) {
            return "UPDATE";
        } else if (upperSql.startsWith(DELETE)) {
            return "DELETE";
        } else if (upperSql.startsWith(SHOW) || upperSql.startsWith(DESCRIBE) || upperSql.startsWith(EXPLAIN)) {
            return "SHOW";
        }
        
        return "OTHER";
    }
    
    /**
     * 移除SQL中的注释
     * @param sql 原始SQL
     * @return 清理后的SQL
     */
    private String removeComments(String sql) {
        // 移除单行注释
        sql = sql.replaceAll("--.*$", "");
        // 移除多行注释
        sql = sql.replaceAll("/\\*[\\s\\S]*?\\*/", "");
        return sql;
    }
    
    /**
     * 判断是否为只读查询
     * @param sqlType SQL类型
     * @return 是否只读
     */
    public boolean isReadOnly(String sqlType) {
        return SELECT.equals(sqlType) || SHOW.equals(sqlType);
    }
    
    /**
     * 构建可视化查询SQL
     * @param request 可视化查询请求
     * @return 构建的SQL
     */
    public String buildVisualizeQuerySql(QueryVisualizeRequest request) {
        StringBuilder sql = new StringBuilder("SELECT ");
        
        // 处理列
        if (request.getColumns() == null || request.getColumns().isEmpty() || 
            request.getColumns().contains("*")) {
            sql.append("*");
        } else {
            sql.append(String.join(", ", request.getColumns()));
        }
        
        sql.append(" FROM ").append(request.getTableName());
        
        // 处理WHERE条件
        if (request.getWhereConditions() != null && !request.getWhereConditions().isEmpty()) {
            sql.append(" WHERE ");
            for (int i = 0; i < request.getWhereConditions().size(); i++) {
                QueryVisualizeRequest.WhereCondition condition = request.getWhereConditions().get(i);
                if (i > 0 && condition.getLogic() != null) {
                    sql.append(" ").append(condition.getLogic()).append(" ");
                }
                buildWhereCondition(sql, condition);
            }
        }
        
        // 处理GROUP BY
        if (request.getGroupBy() != null && !request.getGroupBy().isEmpty()) {
            sql.append(" GROUP BY ").append(String.join(", ", request.getGroupBy()));
        }
        
        // 处理ORDER BY
        if (request.getOrderBy() != null && !request.getOrderBy().isEmpty()) {
            sql.append(" ORDER BY ");
            for (int i = 0; i < request.getOrderBy().size(); i++) {
                QueryVisualizeRequest.OrderByCondition order = request.getOrderBy().get(i);
                if (i > 0) {
                    sql.append(", ");
                }
                sql.append(order.getColumn()).append(" ").append(order.getDirection());
            }
        }
        
        // 处理分页
        if (request.getPageNum() != null && request.getPageSize() != null) {
            int offset = (request.getPageNum() - 1) * request.getPageSize();
            sql.append(" LIMIT ").append(offset).append(", ").append(request.getPageSize());
        }
        
        return sql.toString();
    }
    
    private void buildWhereCondition(StringBuilder sql, QueryVisualizeRequest.WhereCondition condition) {
        sql.append(condition.getColumn());
        
        switch (condition.getOperator()) {
            case "=":
                sql.append(" = ").append(formatValue(condition.getValue()));
                break;
            case "!=":
                sql.append(" != ").append(formatValue(condition.getValue()));
                break;
            case ">":
                sql.append(" > ").append(formatValue(condition.getValue()));
                break;
            case "<":
                sql.append(" < ").append(formatValue(condition.getValue()));
                break;
            case ">=":
                sql.append(" >= ").append(formatValue(condition.getValue()));
                break;
            case "<=":
                sql.append(" <= ").append(formatValue(condition.getValue()));
                break;
            case "LIKE":
                sql.append(" LIKE ").append(formatValue(condition.getValue()));
                break;
            case "IN":
                sql.append(" IN (").append(formatValue(condition.getValue())).append(")");
                break;
            case "BETWEEN":
                sql.append(" BETWEEN ").append(formatValue(condition.getValue()))
                   .append(" AND ").append(formatValue(condition.getValue2()));
                break;
            case "IS NULL":
                sql.append(" IS NULL");
                break;
            case "IS NOT NULL":
                sql.append(" IS NOT NULL");
                break;
            default:
                sql.append(" = ").append(formatValue(condition.getValue()));
        }
    }
    
    private String formatValue(Object value) {
        if (value == null) {
            return "NULL";
        }
        if (value instanceof Number) {
            return value.toString();
        }
        return "'" + value.toString().replace("'", "''") + "'";
    }
}
