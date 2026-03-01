package com.dbms.mask.algorithm;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.crypto.SecureUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 加密脱敏算法
 * 使用AES加密
 */
@Component
public class EncryptAlgorithmImpl implements MaskAlgorithm {
    
    @Value("${mask.encrypt-key:dbms-mask-secret-key-2024}")
    private String encryptKey;
    
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    
    @Override
    public String mask(String data, String config) {
        if (data == null || data.isEmpty()) {
            return data;
        }
        
        JSONObject configJson = parseConfig(config);
        String key = configJson.getStr("key", encryptKey);
        
        // 使用配置的加密方式
        String encryptType = configJson.getStr("encryptType", "AES");
        
        try {
            switch (encryptType.toUpperCase()) {
                case "BASE64":
                    return base64Encode(data);
                case "AES":
                    return aesEncrypt(data, key);
                case "MD5":
                    return md5Encrypt(data);
                default:
                    return aesEncrypt(data, key);
            }
        } catch (Exception e) {
            // 加密失败返回原文并标记
            return "[加密失败]" + data;
        }
    }
    
    /**
     * AES加密
     */
    private String aesEncrypt(String data, String key) throws Exception {
        // 确保密钥长度为16位
        String validKey = ensureKeyLength(key, 16);
        SecretKeySpec secretKey = new SecretKeySpec(validKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }
    
    /**
     * Base64编码
     */
    private String base64Encode(String data) {
        return Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * MD5加密 (不可逆，仅用于特定场景)
     */
    private String md5Encrypt(String data) {
        return SecureUtil.md5(data);
    }
    
    /**
     * 确保密钥长度
     */
    private String ensureKeyLength(String key, int length) {
        if (key.length() >= length) {
            return key.substring(0, length);
        }
        // 密钥不足则补齐
        StringBuilder sb = new StringBuilder(key);
        while (sb.length() < length) {
            sb.append("0");
        }
        return sb.toString();
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
        return "encrypt";
    }
}
