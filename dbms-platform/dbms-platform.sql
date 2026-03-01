-- 企业数据库管理平台 - 数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS dbms_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE dbms_platform;

-- 1. 数据库实例表
CREATE TABLE IF NOT EXISTS db_instance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    instance_name VARCHAR(100) NOT NULL COMMENT '实例名称',
    instance_type VARCHAR(50) NOT NULL COMMENT '数据库类型:MySQL/Oracle/PostgreSQL/DM/Kingbase',
    host VARCHAR(100) NOT NULL COMMENT '主机地址',
    port INT NOT NULL COMMENT '端口',
    service_name VARCHAR(100) DEFAULT NULL COMMENT '服务名(Oracle/达梦/数据库名)',
    username VARCHAR(100) NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码(加密存储)',
    description VARCHAR(500) DEFAULT NULL COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态:0禁用 1启用',
    created_by VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除:0未删除 1已删除',
    INDEX idx_instance_name (instance_name),
    INDEX idx_instance_type (instance_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据库实例表';

-- 2. 数据库目录(库)表
CREATE TABLE IF NOT EXISTS db_catalog (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    instance_id BIGINT NOT NULL COMMENT '关联实例ID',
    catalog_name VARCHAR(100) NOT NULL COMMENT '库名',
    description VARCHAR(500) DEFAULT NULL COMMENT '描述',
    created_by VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_instance_id (instance_id),
    FOREIGN KEY (instance_id) REFERENCES db_instance(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据库目录表';

-- 3. 系统用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    dept_id BIGINT DEFAULT NULL COMMENT '部门ID',
    status TINYINT DEFAULT 1 COMMENT '状态:0禁用 1启用',
    created_by VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_username (username),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 4. 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    role_code VARCHAR(50) NOT NULL COMMENT '角色编码',
    role_name VARCHAR(100) NOT NULL COMMENT '角色名称',
    description VARCHAR(500) DEFAULT NULL COMMENT '描述',
    created_by VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 5. 权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    perm_code VARCHAR(100) NOT NULL COMMENT '权限编码',
    perm_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    resource_type VARCHAR(20) DEFAULT NULL COMMENT '资源类型:menu/button/api',
    parent_id BIGINT DEFAULT 0 COMMENT '父权限ID',
    created_by VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_perm_code (perm_code),
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 6. 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    created_by VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (user_id, role_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 7. 角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_perm (
    role_id BIGINT NOT NULL COMMENT '角色ID',
    perm_id BIGINT NOT NULL COMMENT '权限ID',
    created_by VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (role_id, perm_id),
    INDEX idx_perm_id (perm_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 8. 数据库访问策略表(细粒度权限)
CREATE TABLE IF NOT EXISTS db_access_policy (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    instance_id BIGINT NOT NULL COMMENT '实例ID',
    catalog_name VARCHAR(100) DEFAULT NULL COMMENT '库名(空表示全库)',
    table_name VARCHAR(100) DEFAULT NULL COMMENT '表名(空表示全表)',
    access_type VARCHAR(20) DEFAULT 'read' COMMENT '访问类型:read/write/admin',
    mask_level VARCHAR(20) DEFAULT 'none' COMMENT '脱敏级别:none/low/high',
    created_by VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_user_id (user_id),
    INDEX idx_instance_id (instance_id),
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE,
    FOREIGN KEY (instance_id) REFERENCES db_instance(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据库访问策略表';

-- 9. 脱敏规则表
CREATE TABLE IF NOT EXISTS mask_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    rule_name VARCHAR(100) NOT NULL COMMENT '规则名称',
    data_type VARCHAR(50) NOT NULL COMMENT '数据类型:idcard/phone/bank/card/email',
    mask_algorithm VARCHAR(50) NOT NULL COMMENT '算法:mask/replace/encrypt/hash',
    mask_config VARCHAR(500) DEFAULT NULL COMMENT '脱敏配置JSON',
    created_by VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_data_type (data_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='脱敏规则表';

-- 10. 备份策略表
CREATE TABLE IF NOT EXISTS backup_policy (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    policy_name VARCHAR(100) NOT NULL COMMENT '策略名称',
    instance_id BIGINT NOT NULL COMMENT '实例ID',
    backup_type VARCHAR(20) DEFAULT 'full' COMMENT '备份类型:full/increment',
    schedule_cron VARCHAR(50) DEFAULT NULL COMMENT '调度cron表达式',
    retain_days INT DEFAULT 30 COMMENT '保留天数',
    enabled TINYINT DEFAULT 1 COMMENT '是否启用:0禁用 1启用',
    created_by VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_instance_id (instance_id),
    FOREIGN KEY (instance_id) REFERENCES db_instance(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='备份策略表';

-- 11. 备份任务表
CREATE TABLE IF NOT EXISTS backup_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    policy_id BIGINT NOT NULL COMMENT '策略ID',
    task_status VARCHAR(20) DEFAULT 'pending' COMMENT '任务状态:pending/running/success/failed',
    start_time DATETIME DEFAULT NULL COMMENT '开始时间',
    end_time DATETIME DEFAULT NULL COMMENT '结束时间',
    backup_file_path VARCHAR(500) DEFAULT NULL COMMENT '备份文件路径',
    error_message TEXT DEFAULT NULL COMMENT '错误信息',
    created_by VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_policy_id (policy_id),
    INDEX idx_task_status (task_status),
    FOREIGN KEY (policy_id) REFERENCES backup_policy(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='备份任务表';

-- 12. 查询日志/审计表
CREATE TABLE IF NOT EXISTS query_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    instance_id BIGINT NOT NULL COMMENT '实例ID',
    sql_text TEXT DEFAULT NULL COMMENT 'SQL文本',
    query_time INT DEFAULT NULL COMMENT '耗时(毫秒)',
    result_rows INT DEFAULT NULL COMMENT '返回行数',
    ip_address VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    user_agent VARCHAR(500) DEFAULT NULL COMMENT '用户代理',
    watermark_info VARCHAR(500) DEFAULT NULL COMMENT '水印信息',
    created_by VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_user_id (user_id),
    INDEX idx_instance_id (instance_id),
    INDEX idx_created_time (created_time),
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE,
    FOREIGN KEY (instance_id) REFERENCES db_instance(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='查询日志表';

-- 插入默认管理员用户 (密码: admin123)
INSERT INTO sys_user (username, password, real_name, email, status) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 'admin@example.com', 1);

-- 插入默认角色
INSERT INTO sys_role (role_code, role_name, description) 
VALUES ('ADMIN', '系统管理员', '拥有所有权限'),
       ('DBA', '数据库管理员', '管理数据库实例'),
       ('USER', '普通用户', '普通查询权限');

-- 插入默认脱敏规则
INSERT INTO mask_rule (rule_name, data_type, mask_algorithm, mask_config) VALUES
('身份证号掩码', 'idcard', 'mask', '{"showLast4": true}'),
('手机号掩码', 'phone', 'mask', '{"showLast4": true}'),
('银行卡号掩码', 'bank', 'mask', '{"showFirst6": true, "showLast4": true}'),
('邮箱脱敏', 'email', 'replace', '{"replaceChar": "*"}');
