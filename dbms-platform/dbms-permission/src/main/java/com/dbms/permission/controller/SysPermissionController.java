package com.dbms.permission.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dbms.permission.dto.PermissionDTO;
import com.dbms.permission.dto.Result;
import com.dbms.permission.entity.SysPermission;
import com.dbms.permission.service.SysPermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 权限管理Controller
 */
@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class SysPermissionController {
    
    private final SysPermissionService sysPermissionService;
    
    /**
     * 分页查询权限列表
     */
    @GetMapping
    public Result<Page<SysPermission>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String permCode,
            @RequestParam(required = false) String permName,
            @RequestParam(required = false) String resourceType) {
        
        Page<SysPermission> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(permCode != null, SysPermission::getPermCode, permCode)
               .like(permName != null, SysPermission::getPermName, permName)
               .eq(resourceType != null, SysPermission::getResourceType, resourceType)
               .orderByDesc(SysPermission::getCreatedTime);
        
        return Result.success(sysPermissionService.page(page, wrapper));
    }
    
    /**
     * 获取所有权限（树形结构）
     */
    @GetMapping("/tree")
    public Result<List<PermissionDTO>> listTree() {
        List<PermissionDTO> tree = sysPermissionService.listAllPermissionsTree();
        return Result.success(tree);
    }
    
    /**
     * 获取所有权限列表
     */
    @GetMapping("/all")
    public Result<List<PermissionDTO>> listAll() {
        List<SysPermission> permissions = sysPermissionService.list();
        List<PermissionDTO> dtos = permissions.stream()
            .map(p -> {
                PermissionDTO dto = new PermissionDTO();
                org.springframework.beans.BeanUtils.copyProperties(p, dto);
                return dto;
            })
            .collect(java.util.stream.Collectors.toList());
        return Result.success(dtos);
    }
    
    /**
     * 根据ID查询权限
     */
    @GetMapping("/{id}")
    public Result<PermissionDTO> getById(@PathVariable Long id) {
        PermissionDTO permission = sysPermissionService.getPermById(id);
        if (permission == null) {
            return Result.error("权限不存在");
        }
        return Result.success(permission);
    }
    
    /**
     * 根据权限编码查询权限
     */
    @GetMapping("/code/{permCode}")
    public Result<PermissionDTO> getByCode(@PathVariable String permCode) {
        PermissionDTO permission = sysPermissionService.getPermByCode(permCode);
        if (permission == null) {
            return Result.error("权限不存在");
        }
        return Result.success(permission);
    }
    
    /**
     * 获取子权限列表
     */
    @GetMapping("/{id}/children")
    public Result<List<PermissionDTO>> getChildren(@PathVariable Long id) {
        List<PermissionDTO> children = sysPermissionService.getChildPermissions(id);
        return Result.success(children);
    }
    
    /**
     * 创建权限
     */
    @PostMapping
    public Result<Void> create(@Valid @RequestBody PermissionDTO permissionDTO) {
        try {
            sysPermissionService.createPerm(permissionDTO);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新权限
     */
    @PutMapping
    public Result<Void> update(@Valid @RequestBody PermissionDTO permissionDTO) {
        try {
            sysPermissionService.updatePerm(permissionDTO);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            sysPermissionService.deletePerm(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
