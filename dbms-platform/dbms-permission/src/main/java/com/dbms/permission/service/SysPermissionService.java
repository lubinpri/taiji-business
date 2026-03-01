package com.dbms.permission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dbms.permission.dto.PermissionDTO;
import com.dbms.permission.entity.SysPermission;
import java.util.List;

/**
 * 权限Service接口
 */
public interface SysPermissionService extends IService<SysPermission> {
    
    /**
     * 根据权限编码查询权限
     */
    PermissionDTO getPermByCode(String permCode);
    
    /**
     * 根据权限ID查询权限
     */
    PermissionDTO getPermById(Long id);
    
    /**
     * 创建权限
     */
    boolean createPerm(PermissionDTO permissionDTO);
    
    /**
     * 更新权限
     */
    boolean updatePerm(PermissionDTO permissionDTO);
    
    /**
     * 删除权限
     */
    boolean deletePerm(Long id);
    
    /**
     * 获取所有权限列表（树形结构）
     */
    List<PermissionDTO> listAllPermissionsTree();
    
    /**
     * 获取子权限列表
     */
    List<PermissionDTO> getChildPermissions(Long parentId);
}
