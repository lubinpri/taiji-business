package com.dbms.watermark.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dbms.watermark.dto.WatermarkConfigRequest;
import com.dbms.watermark.entity.WatermarkConfig;

import java.util.List;

/**
 * 水印配置服务接口
 */
public interface WatermarkConfigService extends IService<WatermarkConfig> {
    
    /**
     * 创建水印配置
     */
    WatermarkConfig createConfig(WatermarkConfigRequest request);
    
    /**
     * 更新水印配置
     */
    WatermarkConfig updateConfig(Long id, WatermarkConfigRequest request);
    
    /**
     * 获取启用的水印配置
     */
    WatermarkConfig getEnabledConfig();
    
    /**
     * 根据类型获取水印配置
     */
    WatermarkConfig getConfigByType(String watermarkType);
    
    /**
     * 获取所有配置列表
     */
    List<WatermarkConfig> listConfigs();
    
    /**
     * 启用/禁用配置
     */
    boolean setEnabled(Long id, boolean enabled);
}
