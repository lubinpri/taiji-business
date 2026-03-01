package com.dbms.mask.algorithm;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Component;

/**
 * 替换脱敏算法
 * 例如: 13012345678 -> [手机号]
 */
@Component
public class ReplaceAlgorithmImpl implements MaskAlgorithm {
    
    @Override
    public String mask(String data, String config) {
        if (data == null || data.isEmpty()) {
            return data;
        }
        
        JSONObject configJson = parseConfig(config);
        String replaceText = configJson.getStr("replaceText", "[已脱敏]");
        
        // 根据数据类型返回对应类型描述
        String dataType = configJson.getStr("dataType", "");
        switch (dataType) {
            case "idcard":
                return "[身份证号]";
            case "phone":
                return "[手机号]";
            case "bank_card":
                return "[银行卡号]";
            case "email":
                return "[邮箱地址]";
            case "name":
                return "[姓名]";
            case "address":
                return "[地址]";
            default:
                return replaceText;
        }
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
        return "replace";
    }
}
