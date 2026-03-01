package com.dbms.resource.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dbms.common.dto.Result;
import com.dbms.common.entity.DbInstance;
import com.dbms.resource.service.DbInstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据库实例管理
 */
@RestController
@RequestMapping("/api/resource/instances")
@RequiredArgsConstructor
public class DbInstanceController {
    
    private final DbInstanceService dbInstanceService;
    
    /**
     * 新增实例
     */
    @PostMapping
    public Result<Boolean> add(@RequestBody DbInstance instance) {
        return Result.success(dbInstanceService.addInstance(instance));
    }
    
    /**
     * 获取实例列表
     */
    @GetMapping
    public Result<Page<DbInstance>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String instanceName,
            @RequestParam(required = false) String instanceType
    ) {
        Page<DbInstance> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<DbInstance> wrapper = new LambdaQueryWrapper<>();
        if (instanceName != null) {
            wrapper.like(DbInstance::getInstanceName, instanceName);
        }
        if (instanceType != null) {
            wrapper.eq(DbInstance::getInstanceType, instanceType);
        }
        
        // 不返回密码
        wrapper.select(DbInstance.class, info -> !"password".equals(info.getProperty()));
        
        return Result.success(dbInstanceService.page(page, wrapper));
    }
    
    /**
     * 获取实例详情
     */
    @GetMapping("/{id}")
    public Result<DbInstance> get(@PathVariable Long id) {
        return Result.success(dbInstanceService.getInstanceDetail(id));
    }
    
    /**
     * 更新实例
     */
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody DbInstance instance) {
        instance.setId(id);
        return Result.success(dbInstanceService.updateInstance(instance));
    }
    
    /**
     * 删除实例
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(dbInstanceService.deleteInstance(id));
    }
    
    /**
     * 测试连接
     */
    @PostMapping("/{id}/test")
    public Result<Boolean> testConnection(@PathVariable Long id) {
        return Result.success(dbInstanceService.testConnection(id));
    }
    
    /**
     * 获取表列表
     */
    @GetMapping("/{id}/tables")
    public Result<List<String>> getTables(
            @PathVariable Long id,
            @RequestParam(required = false) String catalog
    ) {
        return Result.success(dbInstanceService.getTables(id, catalog));
    }
    
    /**
     * 获取字段列表
     */
    @GetMapping("/{id}/columns")
    public Result<List<?>> getColumns(
            @PathVariable Long id,
            @RequestParam String catalog,
            @RequestParam String table
    ) {
        return Result.success(dbInstanceService.getColumns(id, catalog, table));
    }
}
