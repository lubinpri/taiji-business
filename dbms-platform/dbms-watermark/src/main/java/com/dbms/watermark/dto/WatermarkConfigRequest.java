package com.dbms.watermark.dto;

import lombok.Data;

/**
 * 水印配置请求DTO
 */
@Data
public class WatermarkConfigRequest {
    
    private String configName;
    
    private String watermarkType;
    
    private String textContent;
    
    private String imagePath;
    
    private Integer fontSize;
    
    private String fontColor;
    
    private Double alpha;
    
    private Integer rotation;
    
    private Boolean enabled;
}
