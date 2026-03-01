-- 脱敏规则表
CREATE TABLE IF NOT EXISTS `mask_rule` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `rule_name` VARCHAR(100) NOT NULL COMMENT '规则名称',
    `data_type` VARCHAR(50) NOT NULL COMMENT '数据类型: idcard/phone/bank_card/email/name/address',
    `mask_algorithm` VARCHAR(50) NOT NULL COMMENT '算法: mask/replace/encrypt/hash',
    `mask_config` TEXT COMMENT '脱敏配置JSON',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `enabled` TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_data_type` (`data_type`),
    KEY `idx_enabled` (`enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='脱敏规则表';

-- 初始化默认脱敏规则
INSERT INTO `mask_rule` (`rule_name`, `data_type`, `mask_algorithm`, `mask_config`, `description`, `enabled`) VALUES
('身份证号掩码', 'idcard', 'mask', '{"prefixKeep":6,"suffixKeep":4,"maskChar":"*"}', '身份证号脱敏，保留前6位和后4位', 1),
('手机号掩码', 'phone', 'mask', '{"prefixKeep":3,"suffixKeep":4,"maskChar":"*"}', '手机号脱敏，保留前3位和后4位', 1),
('银行卡号掩码', 'bank_card', 'mask', '{}', '银行卡号脱敏，每4位一组，中间掩码', 1),
('邮箱掩码', 'email', 'mask', '{}', '邮箱脱敏，保留首尾字符', 1),
('姓名掩码', 'name', 'replace', '{"replaceText":"[姓名]"}', '姓名替换', 1),
('地址掩码', 'address', 'replace', '{"replaceText":"[地址]"}', '地址替换', 1);
