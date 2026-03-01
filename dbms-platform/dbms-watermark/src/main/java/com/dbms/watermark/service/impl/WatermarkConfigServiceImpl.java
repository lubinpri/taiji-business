package com.dbms.watermark.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dbms.watermark.dto.WatermarkConfigRequest;
import com.dbms.watermark.entity.WatermarkConfig;
import com.dbms.watermark.mapper.WatermarkConfigMapper;
import com.dbms.watermark.service.WatermarkConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 水印配置服务实现
 */
@Service
public class WatermarkConfigServiceImpl extends ServiceImpl<WatermarkConfigMapper, WatermarkConfig>
        implements WatermarkConfigService {
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public WatermarkConfig createConfig(WatermarkConfigRequest request) {
        WatermarkConfig config = new WatermarkConfig();
        BeanUtils.copyProperties(request, config);
        config.setCreateTime(LocalDateTime.now());
        config.setUpdateTime(LocalDateTime.now());
        
        // 如果是启用状态，先禁用其他配置
        if (Boolean.TRUE.equals(config.getEnabled())) {
            disableOtherConfigs(null);
        }
        
        this.save(config);
        return config;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public WatermarkConfig updateConfig(Long id, WatermarkConfigRequest request) {
        WatermarkConfig config = this.getById(id);
        if (config == null) {
            throw new RuntimeException("水印配置不存在: " + id);
        }
        
        BeanUtils.copyProperties(request, config, "id", "createTime");
        config.setUpdateTime(LocalDateTime.now());
        
        // 如果是启用状态，先禁用其他配置
        if (Boolean.TRUE.equals(config.getEnabled())) {
            disableOtherConfigs(id);
        }
        
        this.updateById(config);
        return config;
    }
    
    @Override
    public WatermarkConfig getEnabledConfig() {
        return this.getOne(new LambdaQueryWrapper<WatermarkConfig>()
                .eq(WatermarkConfig::getEnabled, true));
    }
    
    @Override
    public WatermarkConfig getConfigByType(String watermarkType) {
        return this.getOne(new LambdaQueryWrapper<WatermarkConfig>()
                .eq(WatermarkConfig::getWatermarkType, watermarkType));
    }
    
    @Override
    public List<WatermarkConfig> listConfigs() {
        return this.list(new LambdaQueryWrapper<WatermarkConfig>()
                .orderByDesc(WatermarkConfig::getCreateTime));
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setEnabled(Long id, boolean enabled) {
        WatermarkConfig config = this.getById(id);
        if (config == null) {
            throw new RuntimeException("水印配置不存在: " + id);
        }
        
        if (enabled) {
            disableOtherConfigs(id);
        }
        
        config.setEnabled(enabled);
        config.setUpdateTime(LocalDateTime.now());
        return this.updateById(config);
    }
    
    /**
     * 禁用其他配置
     */
    private void disableOtherConfigs(Long excludeId) {
        LambdaQueryWrapper<WatermarkConfig> wrapper = new LambdaQueryWrapper<WatermarkConfig>()
                .eq(WatermarkConfig::getEnabled, true);
        
        if (excludeId != null) {
            wrapper.ne(WatermarkConfig::getId, excludeId);
        }
        
        List<WatermarkConfig> enabledConfigs = this.list(wrapper);
        for (WatermarkConfig c : enabledConfigs) {
            c.setEnabled(false);
            c.setUpdateTime(LocalDateTime.now());
        }
        
        if (!enabledConfigs.isEmpty()) {
            this.updateBatchById(enabledConfigs);
        }
    }
}
