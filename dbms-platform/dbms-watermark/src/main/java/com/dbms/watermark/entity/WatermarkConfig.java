package com.dbms.watermark.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 水印配置实体
 */
@Data
@TableName("watermark_config")
public class WatermarkConfig {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 配置名称
     */
    private String configName;
    
    /**
     * 水印类型: TEXT(文字), IMAGE(图片), INVISIBLE(不可见)
     */
    private String watermarkType;
    
    /**
     * 文字水印内容
     */
    private String textContent;
    
    /**
     * 图片水印路径
     */
    private String imagePath;
    
    /**
     * 字体大小
     */
    private Integer fontSize;
    
    /**
     * 字体颜色
     */
    private String fontColor;
    
    /**
     * 透明度 0-1
     */
    private Double alpha;
    
    /**
     * 旋转角度
     */
    private Integer rotation;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
