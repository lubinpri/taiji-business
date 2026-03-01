package com.dbms.resource.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dbms.common.entity.DbInstance;

import java.util.List;

public interface DbInstanceService extends IService<DbInstance> {
    
    /**
     * 新增数据库实例
     */
    boolean addInstance(DbInstance instance);
    
    /**
     * 更新数据库实例
     */
    boolean updateInstance(DbInstance instance);
    
    /**
     * 删除数据库实例
     */
    boolean deleteInstance(Long id);
    
    /**
     * 测试数据库连接
     */
    boolean testConnection(Long id);
    
    /**
     * 获取实例详情
     */
    DbInstance getInstanceDetail(Long id);
    
    /**
     * 获取表列表
     */
    List<String> getTables(Long id, String catalog);
    
    /**
     * 获取字段列表
     */
    List<?> getColumns(Long id, String catalog, String table);
}
