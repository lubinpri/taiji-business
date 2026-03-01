package com.dbms.mask.algorithm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 脱敏算法工厂
 */
@Component
public class MaskAlgorithmFactory {
    
    private final Map<String, MaskAlgorithm> algorithmMap = new HashMap<>();
    
    @Autowired
    public MaskAlgorithmFactory(List<MaskAlgorithm> algorithms) {
        for (MaskAlgorithm algorithm : algorithms) {
            algorithmMap.put(algorithm.getType(), algorithm);
        }
    }
    
    /**
     * 获取算法实例
     * @param type 算法类型: mask/replace/encrypt/hash
     * @return 算法实例
     */
    public MaskAlgorithm getAlgorithm(String type) {
        MaskAlgorithm algorithm = algorithmMap.get(type);
        if (algorithm == null) {
            // 默认使用掩码算法
            return algorithmMap.get("mask");
        }
        return algorithm;
    }
    
    /**
     * 判断算法是否存在
     * @param type 算法类型
     * @return 是否存在
     */
    public boolean hasAlgorithm(String type) {
        return algorithmMap.containsKey(type);
    }
    
    /**
     * 获取所有支持的算法类型
     * @return 算法类型列表
     */
    public List<String> getSupportedAlgorithms() {
        return List.of("mask", "replace", "encrypt", "hash");
    }
}
