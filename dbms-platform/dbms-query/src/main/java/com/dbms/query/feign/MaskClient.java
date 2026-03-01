package com.dbms.query.feign;

import com.dbms.common.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 脱敏服务Feign客户端
 */
@FeignClient(name = "dbms-mask", path = "/api/mask")
public interface MaskClient {
    
    /**
     * 对查询结果进行脱敏处理
     * @param data 原始数据(列名-值对列表)
     * @param database 数据库名
     * @param table 表名
     * @return 脱敏后的数据
     */
    @PostMapping("/process")
    Result<List<Map<String, Object>>> maskData(
            @RequestBody List<Map<String, Object>> data,
            @RequestParam("database") String database,
            @RequestParam("table") String table
    );
    
    /**
     * 对单行数据进行脱敏处理
     * @param row 原始数据(列名-值对)
     * @param database 数据库名
     * @param table 表名
     * @return 脱敏后的数据
     */
    @PostMapping("/process/row")
    Result<Map<String, Object>> maskRow(
            @RequestBody Map<String, Object> row,
            @RequestParam("database") String database,
            @RequestParam("table") String table
    );
    
    /**
     * 检查字段是否需要脱敏
     * @param database 数据库名
     * @param table 表名
     * @param column 列名
     * @return 是否需要脱敏
     */
    @PostMapping("/check")
    Result<Boolean> checkMaskRequired(
            @RequestParam("database") String database,
            @RequestParam("table") String table,
            @RequestParam("column") String column
    );
}
