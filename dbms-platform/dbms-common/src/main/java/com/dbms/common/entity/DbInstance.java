package com.dbms.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 数据库实例
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("db_instance")
public class DbInstance extends BaseEntity {
    
    /**
     * 实例名称
     */
    private String instanceName;
    
    /**
     * 数据库类型: MySQL/Oracle/PostgreSQL/DM/Kingbase
     */
    private String instanceType;
    
    /**
     * 主机地址
     */
    private String host;
    
    /**
     * 端口
     */
    private Integer port;
    
    /**
     * 服务名(Oracle/达梦)
     */
    private String serviceName;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码(加密存储)
     */
    private String password;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 状态: 0禁用 1启用
     */
    private Integer status;
}
