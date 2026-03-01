package com.dbms.query.feign;

import com.dbms.common.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 权限服务Feign客户端
 */
@FeignClient(name = "dbms-permission", path = "/api/permission")
public interface PermissionClient {
    
    /**
     * 校验查询权限
     * @param userId 用户ID
     * @param instanceId 实例ID
     * @param database 数据库名
     * @param table 表名(可空)
     * @param sqlType SQL类型
     * @return 校验结果
     */
    @PostMapping("/check")
    Result<Boolean> checkQueryPermission(
            @RequestParam("userId") Long userId,
            @RequestParam("instanceId") Long instanceId,
            @RequestParam("database") String database,
            @RequestParam("table") String table,
            @RequestParam("sqlType") String sqlType
    );
    
    /**
     * 批量校验权限
     * @param userId 用户ID
     * @param checks 权限检查项
     * @return 权限检查结果
     */
    @PostMapping("/check/batch")
    Result<Map<String, Boolean>> batchCheckPermission(
            @RequestParam("userId") Long userId,
            @RequestBody List<PermissionCheck> checks
    );
    
    /**
     * 权限检查项
     */
    class PermissionCheck {
        private Long instanceId;
        private String database;
        private String table;
        private String sqlType;
        
        public Long getInstanceId() { return instanceId; }
        public void setInstanceId(Long instanceId) { this.instanceId = instanceId; }
        public String getDatabase() { return database; }
        public void setDatabase(String database) { this.database = database; }
        public String getTable() { return table; }
        public void setTable(String table) { this.table = table; }
        public String getSqlType() { return sqlType; }
        public void setSqlType(String sqlType) { this.sqlType = sqlType; }
    }
}
