package com.dbms.permission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dbms.permission.dto.RoleDTO;
import com.dbms.permission.entity.SysRole;
import java.util.List;

/**
 * 角色Service接口
 */
public interface SysRoleService extends IService<SysRole> {
    
    /**
     * 根据角色编码查询角色
     */
    RoleDTO getRoleByCode(String roleCode);
    
    /**
     * 根据角色ID查询角色（含权限）
     */
    RoleDTO getRoleById(Long id);
    
    /**
     * 创建角色
     */
    boolean createRole(RoleDTO roleDTO);
    
    /**
     * 更新角色
     */
    boolean updateRole(RoleDTO roleDTO);
    
    /**
     * 删除角色
     */
    boolean deleteRole(Long id);
    
    /**
     * 分配角色权限
     */
    boolean assignPermissions(Long roleId, List<Long> permIds);
    
    /**
     * 获取角色权限列表
     */
    List<Long> getRolePermIds(Long roleId);
    
    /**
     * 获取所有角色列表
     */
    List<RoleDTO> listAllRoles();
}
