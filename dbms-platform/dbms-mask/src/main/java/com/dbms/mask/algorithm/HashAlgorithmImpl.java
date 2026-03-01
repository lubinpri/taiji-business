package com.dbms.mask.algorithm;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.crypto.SecureUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 哈希脱敏算法
 * 使用SHA-256或MD5进行哈希处理
 */
@Component
public class HashAlgorithmImpl implements MaskAlgorithm {
    
    @Value("${mask.hash-salt:dbms-mask-hash-salt}")
    private String hashSalt;
    
    @Override
    public String mask(String data, String config) {
        if (data == null || data.isEmpty()) {
            return data;
        }
        
        JSONObject configJson = parseConfig(config);
        String salt = configJson.getStr("salt", hashSalt);
        String hashType = configJson.getStr("hashType", "SHA256");
        
        // 组合盐值和数据
        String dataWithSalt = salt + data;
        
        switch (hashType.toUpperCase()) {
            case "MD5":
                return SecureUtil.md5(dataWithSalt);
            case "SHA1":
                return SecureUtil.sha1(dataWithSalt);
            case "SHA256":
                return SecureUtil.sha256(dataWithSalt);
            case "SHA512":
                return SecureUtil.sha512(dataWithSalt);
            default:
                return SecureUtil.sha256(dataWithSalt);
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
        return "hash";
    }
}
