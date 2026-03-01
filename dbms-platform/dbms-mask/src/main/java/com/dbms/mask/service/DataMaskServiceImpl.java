package com.dbms.mask.service;

import com.dbms.mask.algorithm.MaskAlgorithm;
import com.dbms.mask.algorithm.MaskAlgorithmFactory;
import com.dbms.mask.entity.MaskRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据脱敏服务实现
 */
@Service
public class DataMaskServiceImpl implements DataMaskService {
    
    @Autowired
    private MaskAlgorithmFactory algorithmFactory;
    
    @Autowired
    private MaskRuleService maskRuleService;
    
    /**
     * 数据类型到默认算法的映射
     */
    private static final Map<String, String> DEFAULT_ALGORITHM = new HashMap<>();
    static {
        DEFAULT_ALGORITHM.put("idcard", "mask");
        DEFAULT_ALGORITHM.put("phone", "mask");
        DEFAULT_ALGORITHM.put("bank_card", "mask");
        DEFAULT_ALGORITHM.put("email", "mask");
        DEFAULT_ALGORITHM.put("name", "mask");
        DEFAULT_ALGORITHM.put("address", "mask");
    }
    
    @Override
    public String mask(String data, String dataType, String algorithm, String config) {
        if (data == null || data.isEmpty()) {
            return data;
        }
        
        // 构建配置JSON
        String fullConfig = buildConfig(dataType, config);
        
        // 获取算法
        MaskAlgorithm maskAlgorithm = algorithmFactory.getAlgorithm(algorithm);
        return maskAlgorithm.mask(data, fullConfig);
    }
    
    @Override
    public List<String> batchMask(List<String> dataList, String dataType, String algorithm, String config) {
        if (dataList == null || dataList.isEmpty()) {
            return dataList;
        }
        
        return dataList.stream()
                .map(data -> mask(data, dataType, algorithm, config))
                .collect(Collectors.toList());
    }
    
    @Override
    public String maskWithRule(String data, Long ruleId) {
        if (data == null || data.isEmpty()) {
            return data;
        }
        
        MaskRule rule = maskRuleService.getById(ruleId);
        if (rule == null) {
            throw new RuntimeException("规则不存在: " + ruleId);
        }
        
        return mask(data, rule.getDataType(), rule.getMaskAlgorithm(), rule.getMaskConfig());
    }
    
    @Override
    public String autoMask(String data, String dataType) {
        if (data == null || data.isEmpty()) {
            return data;
        }
        
        // 尝试获取对应数据类型的启用规则
        MaskRule rule = maskRuleService.getRuleByDataType(dataType);
        if (rule != null) {
            return mask(data, rule.getDataType(), rule.getMaskAlgorithm(), rule.getMaskConfig());
        }
        
        // 使用默认算法
        String algorithm = DEFAULT_ALGORITHM.getOrDefault(dataType, "mask");
        String config = buildConfig(dataType, null);
        MaskAlgorithm maskAlgorithm = algorithmFactory.getAlgorithm(algorithm);
        return maskAlgorithm.mask(data, config);
    }
    
    /**
     * 构建完整配置JSON
     */
    private String buildConfig(String dataType, String customConfig) {
        if (customConfig == null || customConfig.isEmpty()) {
            return "{\"dataType\":\"" + dataType + "\"}";
        }
        
        // 合并dataType到配置中
        if (!customConfig.contains("dataType")) {
            // 简单处理：追加dataType
            if (customConfig.trim().startsWith("{")) {
                return customConfig.substring(0, customConfig.length() - 1) 
                       + ",\"dataType\":\"" + dataType + "\"}";
            }
            return "{\"dataType\":\"" + dataType + "\",\"custom\":" + customConfig + "}";
        }
        return customConfig;
    }
}
