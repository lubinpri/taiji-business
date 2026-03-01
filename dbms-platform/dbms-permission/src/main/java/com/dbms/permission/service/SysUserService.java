package com.dbms.permission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dbms.permission.dto.UserDTO;
import com.dbms.permission.entity.SysUser;
import java.util.List;

/**
 * 用户Service接口
 */
public interface SysUserService extends IService<SysUser> {
    
    /**
     * 用户登录
     */
    UserDTO login(String username, String password);
    
    /**
     * 根据用户名查询用户
     */
    UserDTO getUserByUsername(String username);
    
    /**
     * 根据用户ID查询用户（含角色和权限）
     */
    UserDTO getUserById(Long id);
    
    /**
     * 创建用户
     */
    boolean createUser(UserDTO userDTO);
    
    /**
     * 更新用户
     */
    boolean updateUser(UserDTO userDTO);
    
    /**
     * 删除用户
     */
    boolean deleteUser(Long id);
    
    /**
     * 分配用户角色
     */
    boolean assignRoles(Long userId, List<Long> roleIds);
    
    /**
     * 获取用户角色列表
     */
    List<Long> getUserRoleIds(Long userId);
    
    /**
     * 获取用户权限列表
     */
    List<String> getUserPermissions(Long userId);
    
    /**
     * 检查用户是否拥有指定权限
     */
    boolean hasPermission(Long userId, String permission);
    
    /**
     * 检查用户是否拥有指定角色
     */
    boolean hasRole(Long userId, String roleCode);
    
    /**
     * 修改密码
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 重置密码
     */
    boolean resetPassword(Long userId, String newPassword);
}
