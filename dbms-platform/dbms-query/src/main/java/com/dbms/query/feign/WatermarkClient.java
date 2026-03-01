package com.dbms.query.feign;

import com.dbms.common.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 水印服务Feign客户端
 */
@FeignClient(name = "dbms-watermark", path = "/api/watermark")
public interface WatermarkClient {
    
    /**
     * 为查询结果添加水印
     * @param data 原始数据
     * @param userId 用户ID
     * @param userName 用户名
     * @return 添加水印后的数据
     */
    @PostMapping("/add")
    Result<List<Map<String, Object>>> addWatermark(
            @RequestBody List<Map<String, Object>> data,
            @RequestParam("userId") Long userId,
            @RequestParam("userName") String userName
    );
    
    /**
     * 为单行数据添加水印
     * @param row 原始数据
     * @param userId 用户ID
     * @param userName 用户名
     * @return 添加水印后的数据
     */
    @PostMapping("/add/row")
    Result<Map<String, Object>> addRowWatermark(
            @RequestBody Map<String, Object> row,
            @RequestParam("userId") Long userId,
            @RequestParam("userName") String userName
    );
    
    /**
     * 为文本添加水印
     * @param text 原始文本
     * @param userId 用户ID
     * @return 添加水印后的文本
     */
    @PostMapping("/add/text")
    Result<String> addTextWatermark(
            @RequestParam("text") String text,
            @RequestParam("userId") Long userId
    );
}
