package com.dbms.mask;

import com.dbms.mask.algorithm.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 脱敏算法单元测试
 */
@SpringBootTest
public class MaskAlgorithmTest {
    
    @Autowired
    private MaskAlgorithmFactory algorithmFactory;
    
    @Test
    void testMaskAlgorithm() {
        MaskAlgorithm algorithm = algorithmFactory.getAlgorithm("mask");
        
        // 测试手机号掩码
        String phone = "13012345678";
        String masked = algorithm.mask(phone, "{\"dataType\":\"phone\"}");
        assertEquals("130****5678", masked);
        
        // 测试身份证掩码
        String idcard = "110101199001011234";
        masked = algorithm.mask(idcard, "{\"dataType\":\"idcard\"}");
        assertEquals("110101********1234", masked);
        
        // 测试银行卡掩码
        String bankCard = "6222021234567890123";
        masked = algorithm.mask(bankCard, "{\"dataType\":\"bank_card\"}");
        assertTrue(masked.contains("*"));
        
        // 测试邮箱掩码
        String email = "test@example.com";
        masked = algorithm.mask(email, "{\"dataType\":\"email\"}");
        assertEquals("t***t@example.com", masked);
    }
    
    @Test
    void testReplaceAlgorithm() {
        MaskAlgorithm algorithm = algorithmFactory.getAlgorithm("replace");
        
        String masked = algorithm.mask("13012345678", "{\"dataType\":\"phone\"}");
        assertEquals("[手机号]", masked);
        
        masked = algorithm.mask("110101199001011234", "{\"dataType\":\"idcard\"}");
        assertEquals("[身份证号]", masked);
    }
    
    @Test
    void testEncryptAlgorithm() {
        MaskAlgorithm algorithm = algorithmFactory.getAlgorithm("encrypt");
        
        String data = "13012345678";
        String encrypted = algorithm.mask(data, "{\"encryptType\":\"AES\"}");
        
        // 加密后不应该等于原文
        assertNotEquals(data, encrypted);
        assertNotNull(encrypted);
    }
    
    @Test
    void testHashAlgorithm() {
        MaskAlgorithm algorithm = algorithmFactory.getAlgorithm("hash");
        
        String data = "13012345678";
        String hashed = algorithm.mask(data, "{\"hashType\":\"SHA256\"}");
        
        // 哈希后应该是64位十六进制字符串
        assertEquals(64, hashed.length());
        assertTrue(hashed.matches("[0-9a-f]+"));
    }
    
    @Test
    void testAlgorithmFactory() {
        assertTrue(algorithmFactory.hasAlgorithm("mask"));
        assertTrue(algorithmFactory.hasAlgorithm("replace"));
        assertTrue(algorithmFactory.hasAlgorithm("encrypt"));
        assertTrue(algorithmFactory.hasAlgorithm("hash"));
        
        assertEquals(4, algorithmFactory.getSupportedAlgorithms().size());
    }
}
