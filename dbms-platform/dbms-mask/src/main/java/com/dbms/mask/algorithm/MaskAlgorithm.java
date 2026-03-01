package com.dbms.mask.algorithm;

/**
 * 脱敏算法接口
 */
public interface MaskAlgorithm {
    
    /**
     * 脱敏处理
     * @param data 原始数据
     * @param config 脱敏配置 (JSON格式)
     * @return 脱敏后的数据
     */
    String mask(String data, String config);
    
    /**
     * 获取算法类型
     * @return 算法类型
     */
    String getType();
}
