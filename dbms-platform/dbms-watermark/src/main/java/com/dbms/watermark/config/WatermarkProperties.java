package com.dbms.watermark.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 水印配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "watermark")
public class WatermarkProperties {
    
    /**
     * 是否启用水印
     */
    private boolean enabled = true;
    
    /**
     * 默认水印类型
     */
    private String defaultType = "TEXT";
    
    /**
     * 文字水印配置
     */
    private TextConfig text = new TextConfig();
    
    /**
     * 图片水印配置
     */
    private ImageConfig image = new ImageConfig();
    
    /**
     * 不可见水印配置
     */
    private InvisibleConfig invisible = new InvisibleConfig();
    
    /**
     * PDF水印配置
     */
    private PdfConfig pdf = new PdfConfig();
    
    @Data
    public static class TextConfig {
        private String fontFamily = "SimSun";
        private int fontSize = 12;
        private String color = "#888888";
        private double alpha = 0.3;
    }
    
    @Data
    public static class ImageConfig {
        private String path = "classpath:/static/watermark.png";
        private double alpha = 0.2;
    }
    
    @Data
    public static class InvisibleConfig {
        private boolean zeroWidthEncoding = true;
    }
    
    @Data
    public static class PdfConfig {
        private String watermarkText = "Confidential - User: {userId} - Time: {timestamp}";
        private int rotation = 45;
        private int fontSize = 10;
    }
}
