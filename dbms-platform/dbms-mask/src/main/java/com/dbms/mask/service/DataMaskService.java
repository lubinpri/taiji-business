package com.dbms.mask.service;

import java.util.List;

/**
 * 数据脱敏服务接口
 */
public interface DataMaskService {
    
    /**
     * 脱敏单条数据
     * @param data 原始数据
     * @param dataType 数据类型
     * @param algorithm 算法类型
     * @param config 脱敏配置
     * @return 脱敏后的数据
     */
    String mask(String data, String dataType, String algorithm, String config);
    
    /**
     * 批量脱敏数据
     * @param dataList 原始数据列表
     * @param dataType 数据类型
     * @param algorithm 算法类型
     * @param config 脱敏配置
     * @return 脱敏后的数据列表
     */
    List<String> batchMask(List<String> dataList, String dataType, String algorithm, String config);
    
    /**
     * 使用规则脱敏数据
     * @param data 原始数据
     * @param ruleId 规则ID
     * @return 脱敏后的数据
     */
    String maskWithRule(String data, Long ruleId);
    
    /**
     * 根据数据类型自动脱敏
     * @param data 原始数据
     * @param dataType 数据类型
     * @return 脱敏后的数据
     */
    String autoMask(String data, String dataType);
}
