package com.dbms.permission.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dbms.permission.dto.Result;
import com.dbms.permission.dto.UserDTO;
import com.dbms.permission.entity.SysUser;
import com.dbms.permission.service.SysUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 用户管理Controller
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class SysUserController {
    
    private final SysUserService sysUserService;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<UserDTO> login(@RequestBody UserDTO loginRequest) {
        try {
            UserDTO user = sysUserService.login(loginRequest.getUsername(), loginRequest.getPassword());
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 分页查询用户列表
     */
    @GetMapping
    public Result<Page<SysUser>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) Integer status) {
        
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(username != null, SysUser::getUsername, username)
               .like(realName != null, SysUser::getRealName, realName)
               .eq(status != null, SysUser::getStatus, status)
               .orderByDesc(SysUser::getCreatedTime);
        
        return Result.success(sysUserService.page(page, wrapper));
    }
    
    /**
     * 根据ID查询用户
     */
    @GetMapping("/{id}")
    public Result<UserDTO> getById(@PathVariable Long id) {
        UserDTO user = sysUserService.getUserById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }
    
    /**
     * 根据用户名查询用户
     */
    @GetMapping("/username/{username}")
    public Result<UserDTO> getByUsername(@PathVariable String username) {
        UserDTO user = sysUserService.getUserByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }
    
    /**
     * 创建用户
     */
    @PostMapping
    public Result<Void> create(@Valid @RequestBody UserDTO userDTO) {
        try {
            sysUserService.createUser(userDTO);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新用户
     */
    @PutMapping
    public Result<Void> update(@Valid @RequestBody UserDTO userDTO) {
        try {
            sysUserService.updateUser(userDTO);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysUserService.deleteUser(id);
        return Result.success();
    }
    
    /**
     * 分配用户角色
     */
    @PostMapping("/{id}/roles")
    public Result<Void> assignRoles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        sysUserService.assignRoles(id, roleIds);
        return Result.success();
    }
    
    /**
     * 获取用户角色列表
     */
    @GetMapping("/{id}/roles")
    public Result<List<Long>> getUserRoles(@PathVariable Long id) {
        List<Long> roleIds = sysUserService.getUserRoleIds(id);
        return Result.success(roleIds);
    }
    
    /**
     * 获取用户权限列表
     */
    @GetMapping("/{id}/permissions")
    public Result<List<String>> getUserPermissions(@PathVariable Long id) {
        List<String> permissions = sysUserService.getUserPermissions(id);
        return Result.success(permissions);
    }
    
    /**
     * 修改密码
     */
    @PostMapping("/{id}/password")
    public Result<Void> changePassword(
            @PathVariable Long id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        try {
            sysUserService.changePassword(id, oldPassword, newPassword);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 重置密码
     */
    @PostMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestParam(defaultValue = "123456") String newPassword) {
        sysUserService.resetPassword(id, newPassword);
        return Result.success();
    }
    
    /**
     * 启用/禁用用户
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setStatus(status);
        sysUserService.updateById(user);
        return Result.success();
    }
}
