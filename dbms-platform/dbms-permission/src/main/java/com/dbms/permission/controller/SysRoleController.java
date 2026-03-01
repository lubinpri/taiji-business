package com.dbms.permission.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dbms.permission.dto.Result;
import com.dbms.permission.dto.RoleDTO;
import com.dbms.permission.entity.SysRole;
import com.dbms.permission.service.SysRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 角色管理Controller
 */
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class SysRoleController {
    
    private final SysRoleService sysRoleService;
    
    /**
     * 分页查询角色列表
     */
    @GetMapping
    public Result<Page<SysRole>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String roleCode,
            @RequestParam(required = false) String roleName) {
        
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(roleCode != null, SysRole::getRoleCode, roleCode)
               .like(roleName != null, SysRole::getRoleName, roleName)
               .orderByDesc(SysRole::getCreatedTime);
        
        return Result.success(sysRoleService.page(page, wrapper));
    }
    
    /**
     * 获取所有角色列表
     */
    @GetMapping("/all")
    public Result<List<RoleDTO>> listAll() {
        List<RoleDTO> roles = sysRoleService.listAllRoles();
        return Result.success(roles);
    }
    
    /**
     * 根据ID查询角色
     */
    @GetMapping("/{id}")
    public Result<RoleDTO> getById(@PathVariable Long id) {
        RoleDTO role = sysRoleService.getRoleById(id);
        if (role == null) {
            return Result.error("角色不存在");
        }
        return Result.success(role);
    }
    
    /**
     * 根据角色编码查询角色
     */
    @GetMapping("/code/{roleCode}")
    public Result<RoleDTO> getByCode(@PathVariable String roleCode) {
        RoleDTO role = sysRoleService.getRoleByCode(roleCode);
        if (role == null) {
            return Result.error("角色不存在");
        }
        return Result.success(role);
    }
    
    /**
     * 创建角色
     */
    @PostMapping
    public Result<Void> create(@Valid @RequestBody RoleDTO roleDTO) {
        try {
            sysRoleService.createRole(roleDTO);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新角色
     */
    @PutMapping
    public Result<Void> update(@Valid @RequestBody RoleDTO roleDTO) {
        try {
            sysRoleService.updateRole(roleDTO);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysRoleService.deleteRole(id);
        return Result.success();
    }
    
    /**
     * 分配角色权限
     */
    @PostMapping("/{id}/permissions")
    public Result<Void> assignPermissions(@PathVariable Long id, @RequestBody List<Long> permIds) {
        sysRoleService.assignPermissions(id, permIds);
        return Result.success();
    }
    
    /**
     * 获取角色权限列表
     */
    @GetMapping("/{id}/permissions")
    public Result<List<Long>> getRolePermissions(@PathVariable Long id) {
        List<Long> permIds = sysRoleService.getRolePermIds(id);
        return Result.success(permIds);
    }
}
