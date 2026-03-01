package com.dbms.watermark.controller;

import cn.hutool.core.bean.BeanUtil;
import com.dbms.watermark.dto.AddWatermarkRequest;
import com.dbms.watermark.dto.WatermarkConfigRequest;
import com.dbms.watermark.dto.WatermarkInfo;
import com.dbms.watermark.entity.WatermarkConfig;
import com.dbms.watermark.service.QueryWatermarkService;
import com.dbms.watermark.service.WatermarkConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 水印管理控制器
 */
@RestController
@RequestMapping("/api/watermark")
@RequiredArgsConstructor
public class WatermarkController {
    
    private final WatermarkConfigService watermarkConfigService;
    private final QueryWatermarkService queryWatermarkService;
    
    /**
     * 创建水印配置
     */
    @PostMapping("/config")
    public ResponseEntity<Map<String, Object>> createConfig(@RequestBody WatermarkConfigRequest request) {
        WatermarkConfig config = watermarkConfigService.createConfig(request);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", config);
        result.put("message", "水印配置创建成功");
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 更新水印配置
     */
    @PutMapping("/config/{id}")
    public ResponseEntity<Map<String, Object>> updateConfig(
            @PathVariable Long id,
            @RequestBody WatermarkConfigRequest request) {
        WatermarkConfig config = watermarkConfigService.updateConfig(id, request);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", config);
        result.put("message", "水印配置更新成功");
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取所有水印配置
     */
    @GetMapping("/config/list")
    public ResponseEntity<Map<String, Object>> listConfigs() {
        List<WatermarkConfig> configs = watermarkConfigService.listConfigs();
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", configs);
        result.put("total", configs.size());
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取启用的水印配置
     */
    @GetMapping("/config/enabled")
    public ResponseEntity<Map<String, Object>> getEnabledConfig() {
        WatermarkConfig config = watermarkConfigService.getEnabledConfig();
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", config);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 启用/禁用配置
     */
    @PutMapping("/config/{id}/enabled")
    public ResponseEntity<Map<String, Object>> setEnabled(
            @PathVariable Long id,
            @RequestParam boolean enabled) {
        boolean success = watermarkConfigService.setEnabled(id, enabled);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", enabled ? "配置已启用" : "配置已禁用");
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 删除水印配置
     */
    @DeleteMapping("/config/{id}")
    public ResponseEntity<Map<String, Object>> deleteConfig(@PathVariable Long id) {
        boolean success = watermarkConfigService.removeById(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "配置删除成功" : "配置删除失败");
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 为查询结果添加水印
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addWatermark(@RequestBody AddWatermarkRequest request) {
        // 构建水印信息
        WatermarkInfo watermarkInfo = new WatermarkInfo();
        watermarkInfo.setUserId(request.getUserId());
        watermarkInfo.setUserName(request.getUserName());
        watermarkInfo.setSessionId(request.getSessionId());
        watermarkInfo.setClientIp(request.getClientIp());
        watermarkInfo.setTimestamp(System.currentTimeMillis());
        
        String dataType = request.getDataType();
        Object resultData;
        
        try {
            switch (dataType.toUpperCase()) {
                case "TEXT":
                    if ("INVISIBLE".equalsIgnoreCase(request.getWatermarkType())) {
                        resultData = queryWatermarkService.addInvisibleWatermark(
                                request.getDataContent(), watermarkInfo);
                    } else {
                        String watermarkText = request.getWatermarkText();
                        if (!org.springframework.util.StringUtils.hasText(watermarkText)) {
                            watermarkText = watermarkInfo.toWatermarkText();
                        }
                        resultData = queryWatermarkService.addTextWatermark(
                                request.getDataContent(), watermarkText);
                    }
                    break;
                    
                case "JSON":
                    resultData = queryWatermarkService.addJsonWatermark(
                            request.getDataContent(), watermarkInfo, request.getWatermarkType());
                    break;
                    
                case "EXCEL":
                case "PDF":
                    // 二进制数据需要特殊处理，这里返回处理状态
                    resultData = "Binary data watermark processing not fully implemented in REST API";
                    break;
                    
                default:
                    resultData = request.getDataContent();
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", resultData);
            result.put("watermarkInfo", watermarkInfo);
            result.put("message", "水印添加成功");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "水印添加失败: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    /**
     * 提取不可见水印
     */
    @PostMapping("/extract")
    public ResponseEntity<Map<String, Object>> extractWatermark(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        
        WatermarkInfo watermarkInfo = queryWatermarkService.extractInvisibleWatermark(text);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", watermarkInfo != null);
        result.put("data", watermarkInfo);
        result.put("message", watermarkInfo != null ? "水印提取成功" : "未检测到水印");
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 水印服务健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("service", "dbms-watermark");
        result.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(result);
    }
}
