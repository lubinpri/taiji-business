-- 水印配置表
CREATE TABLE IF NOT EXISTS `watermark_config` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `config_name` VARCHAR(100) NOT NULL COMMENT '配置名称',
    `watermark_type` VARCHAR(20) NOT NULL COMMENT '水印类型: TEXT(文字), IMAGE(图片), INVISIBLE(不可见)',
    `text_content` TEXT COMMENT '文字水印内容',
    `image_path` VARCHAR(255) COMMENT '图片水印路径',
    `font_size` INT DEFAULT 12 COMMENT '字体大小',
    `font_color` VARCHAR(20) DEFAULT '#888888' COMMENT '字体颜色',
    `alpha` DOUBLE DEFAULT 0.3 COMMENT '透明度 0-1',
    `rotation` INT DEFAULT 0 COMMENT '旋转角度',
    `enabled` TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_watermark_type` (`watermark_type`),
    KEY `idx_enabled` (`enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='水印配置表';

-- 初始化默认配置
INSERT INTO `watermark_config` (`config_name`, `watermark_type`, `text_content`, `font_size`, `font_color`, `alpha`, `rotation`, `enabled`)
VALUES 
    ('默认文字水印', 'TEXT', 'Confidential - {userId} - {timestamp}', 12, '#888888', 0.3, 0, 1),
    ('默认不可见水印', 'INVISIBLE', NULL, NULL, NULL, NULL, NULL, 0);
