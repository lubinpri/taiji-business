package com.dbms.resource.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dbms.common.entity.DbInstance;
import com.dbms.common.util.DatabaseConnector;
import com.dbms.resource.mapper.DbInstanceMapper;
import com.dbms.resource.service.DbInstanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class DbInstanceServiceImpl extends ServiceImpl<DbInstanceMapper, DbInstance> implements DbInstanceService {
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addInstance(DbInstance instance) {
        // 密码加密存储
        instance.setPassword(encryptPassword(instance.getPassword()));
        return this.save(instance);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateInstance(DbInstance instance) {
        if (instance.getPassword() != null) {
            instance.setPassword(encryptPassword(instance.getPassword()));
        }
        return this.updateById(instance);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteInstance(Long id) {
        return this.removeById(id);
    }
    
    @Override
    public boolean testConnection(Long id) {
        DbInstance instance = this.getById(id);
        if (instance == null) {
            throw new RuntimeException("实例不存在");
        }
        
        // 解密密码进行测试
        String password = decryptPassword(instance.getPassword());
        DbInstance testInstance = new DbInstance();
        testInstance.setInstanceType(instance.getInstanceType());
        testInstance.setHost(instance.getHost());
        testInstance.setPort(instance.getPort());
        testInstance.setServiceName(instance.getServiceName());
        testInstance.setUsername(instance.getUsername());
        testInstance.setPassword(password);
        
        DatabaseConnector connector = new DatabaseConnector(testInstance);
        return connector.testConnection();
    }
    
    @Override
    public DbInstance getInstanceDetail(Long id) {
        DbInstance instance = this.getById(id);
        if (instance != null) {
            // 不返回密码
            instance.setPassword(null);
        }
        return instance;
    }
    
    @Override
    public List<String> getTables(Long id, String catalog) {
        DbInstance instance = this.getById(id);
        if (instance == null) {
            throw new RuntimeException("实例不存在");
        }
        
        String password = decryptPassword(instance.getPassword());
        DbInstance queryInstance = new DbInstance();
        queryInstance.setInstanceType(instance.getInstanceType());
        queryInstance.setHost(instance.getHost());
        queryInstance.setPort(instance.getPort());
        queryInstance.setServiceName(instance.getServiceName());
        queryInstance.setUsername(instance.getUsername());
        queryInstance.setPassword(password);
        
        DatabaseConnector connector = new DatabaseConnector(queryInstance);
        try {
            return connector.getTables(catalog);
        } catch (Exception e) {
            log.error("获取表列表失败: {}", e.getMessage());
            throw new RuntimeException("获取表列表失败: " + e.getMessage());
        } finally {
            connector.close();
        }
    }
    
    @Override
    public List<?> getColumns(Long id, String catalog, String table) {
        DbInstance instance = this.getById(id);
        if (instance == null) {
            throw new RuntimeException("实例不存在");
        }
        
        String password = decryptPassword(instance.getPassword());
        DbInstance queryInstance = new DbInstance();
        queryInstance.setInstanceType(instance.getInstanceType());
        queryInstance.setHost(instance.getHost());
        queryInstance.setPort(instance.getPort());
        queryInstance.setServiceName(instance.getServiceName());
        queryInstance.setUsername(instance.getUsername());
        queryInstance.setPassword(password);
        
        DatabaseConnector connector = new DatabaseConnector(queryInstance);
        try {
            return connector.getColumns(catalog, table);
        } catch (Exception e) {
            log.error("获取字段列表失败: {}", e.getMessage());
            throw new RuntimeException("获取字段列表失败: " + e.getMessage());
        } finally {
            connector.close();
        }
    }
    
    /**
     * 密码加密
     */
    private String encryptPassword(String password) {
        // 简单Base64编码示例，生产环境建议使用AES加密
        return cn.hutool.core.codec.Base64.encode(password);
    }
    
    /**
     * 密码解密
     */
    private String decryptPassword(String encryptedPassword) {
        return cn.hutool.core.codec.Base64.decodeStr(encryptedPassword);
    }
}
