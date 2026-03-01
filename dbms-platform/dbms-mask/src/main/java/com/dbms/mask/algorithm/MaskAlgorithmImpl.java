package com.dbms.mask.algorithm;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Component;

/**
 * 掩码脱敏算法
 * 例如: 13012345678 -> 130****5678
 */
@Component
public class MaskAlgorithmImpl implements MaskAlgorithm {
    
    @Override
    public String mask(String data, String config) {
        if (data == null || data.isEmpty()) {
            return data;
        }
        
        int length = data.length();
        JSONObject configJson = parseConfig(config);
        
        // 默认保留首尾各1位，中间掩码
        int prefixKeep = configJson.getInt("prefixKeep", 1);
        int suffixKeep = configJson.getInt("suffixKeep", 1);
        String maskChar = configJson.getStr("maskChar", "*");
        
        // 根据数据类型自动调整
        String dataType = configJson.getStr("dataType", "");
        
        switch (dataType) {
            case "idcard":
                return maskIdCard(data, configJson);
            case "phone":
                return maskPhone(data, configJson);
            case "bank_card":
                return maskBankCard(data, configJson);
            case "email":
                return maskEmail(data, configJson);
            default:
                return defaultMask(data, prefixKeep, suffixKeep, maskChar);
        }
    }
    
    /**
     * 掩码身份证号
     * 例如: 110101199001011234 -> 110101********1234
     */
    private String maskIdCard(String data, JSONObject config) {
        if (data.length() < 8) {
            return data;
        }
        int prefixKeep = 6;
        int suffixKeep = 4;
        String maskChar = config.getStr("maskChar", "*");
        return defaultMask(data, prefixKeep, suffixKeep, maskChar);
    }
    
    /**
     * 掩码手机号
     * 例如: 13012345678 -> 130****5678
     */
    private String maskPhone(String data, JSONObject config) {
        if (data.length() < 7) {
            return data;
        }
        int prefixKeep = 3;
        int suffixKeep = 4;
        String maskChar = config.getStr("maskChar", "*");
        return defaultMask(data, prefixKeep, suffixKeep, maskChar);
    }
    
    /**
     * 掩码银行卡号
     * 例如: 6222021234567890123 -> 6222 **** **** 0123
     */
    private String maskBankCard(String data, JSONObject config) {
        if (data.length() < 12) {
            return data;
        }
        // 银行卡号每4位一组，中间掩码
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < data.length(); i++) {
            if (i < 4 || i >= data.length() - 4) {
                result.append(data.charAt(i));
            } else if ((i - 4) % 4 == 0) {
                result.append(" ");
            } else if (i >= 4 && i < data.length() - 4) {
                result.append("*");
            }
        }
        return result.toString();
    }
    
    /**
     * 掩码邮箱
     * 例如: test@example.com -> t***t@example.com
     */
    private String maskEmail(String data, JSONObject config) {
        int atIndex = data.indexOf("@");
        if (atIndex <= 1) {
            return data;
        }
        
        String prefix = data.substring(0, atIndex);
        String suffix = data.substring(atIndex);
        
        if (prefix.length() <= 2) {
            return prefix.charAt(0) + "***" + suffix;
        }
        
        return prefix.charAt(0) + "***" + prefix.charAt(prefix.length() - 1) + suffix;
    }
    
    /**
     * 默认掩码方式
     */
    private String defaultMask(String data, int prefixKeep, int suffixKeep, String maskChar) {
        int length = data.length();
        
        if (length <= prefixKeep + suffixKeep) {
            return data;
        }
        
        int maskLength = length - prefixKeep - suffixKeep;
        StringBuilder mask = new StringBuilder();
        for (int i = 0; i < maskLength; i++) {
            mask.append(maskChar);
        }
        
        return data.substring(0, prefixKeep) + mask + data.substring(length - suffixKeep);
    }
    
    private JSONObject parseConfig(String config) {
        if (config == null || config.isEmpty()) {
            return new JSONObject();
        }
        try {
            return JSONUtil.parseObj(config);
        } catch (Exception e) {
            return new JSONObject();
        }
    }
    
    @Override
    public String getType() {
        return "mask";
    }
}
