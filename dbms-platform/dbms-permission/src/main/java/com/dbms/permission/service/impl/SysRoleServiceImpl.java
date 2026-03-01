package com.dbms.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dbms.permission.dto.PermissionDTO;
import com.dbms.permission.dto.RoleDTO;
import com.dbms.permission.entity.SysPermission;
import com.dbms.permission.entity.SysRole;
import com.dbms.permission.entity.SysRolePerm;
import com.dbms.permission.mapper.SysPermissionMapper;
import com.dbms.permission.mapper.SysRoleMapper;
import com.dbms.permission.mapper.SysRolePermMapper;
import com.dbms.permission.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色Service实现
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    
    private final SysRolePermMapper sysRolePermMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysPermissionMapper sysPermissionMapper;
    
    @Override
    public RoleDTO getRoleByCode(String roleCode) {
        SysRole role = baseMapper.selectByRoleCode(roleCode);
        if (role == null) {
            return null;
        }
        return convertToDTO(role);
    }
    
    @Override
    public RoleDTO getRoleById(Long id) {
        SysRole role = baseMapper.selectById(id);
        if (role == null) {
            return null;
        }
        RoleDTO dto = convertToDTO(role);
        
        // 查询角色权限
        List<Long> permIds = sysRoleMapper.selectPermIdsByRoleId(id);
        dto.setPermissionIds(permIds);
        
        // 查询权限编码
        List<String> permCodes = permIds.stream()
            .map(pid -> {
                SysPermission perm = sysPermissionMapper.selectById(pid);
                return perm != null ? perm.getPermCode() : null;
            })
            .filter(code -> code != null)
            .collect(Collectors.toList());
        dto.setPermissionCodes(permCodes);
        
        return dto;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createRole(RoleDTO roleDTO) {
        // 检查角色编码是否存在
        SysRole existingRole = baseMapper.selectByRoleCode(roleDTO.getRoleCode());
        if (existingRole != null) {
            throw new RuntimeException("角色编码已存在");
        }
        
        SysRole role = new SysRole();
        BeanUtils.copyProperties(roleDTO, role);
        
        boolean result = this.save(role);
        
        // 分配权限
        if (result && roleDTO.getPermissionIds() != null && !roleDTO.getPermissionIds().isEmpty()) {
            assignPermissions(role.getId(), roleDTO.getPermissionIds());
        }
        
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(RoleDTO roleDTO) {
        if (roleDTO.getId() == null) {
            throw new RuntimeException("角色ID不能为空");
        }
        
        SysRole role = baseMapper.selectById(roleDTO.getId());
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        
        // 如果修改了角色编码，检查是否重复
        if (!role.getRoleCode().equals(roleDTO.getRoleCode())) {
            SysRole existingRole = baseMapper.selectByRoleCode(roleDTO.getRoleCode());
            if (existingRole != null) {
                throw new RuntimeException("角色编码已存在");
            }
        }
        
        BeanUtils.copyProperties(roleDTO, role, "id", "createdBy", "createdTime");
        
        boolean result = this.updateById(role);
        
        // 更新权限
        if (result && roleDTO.getPermissionIds() != null) {
            assignPermissions(roleDTO.getId(), roleDTO.getPermissionIds());
        }
        
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(Long id) {
        return this.removeById(id);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignPermissions(Long roleId, List<Long> permIds) {
        // 删除角色现有权限
        sysRolePermMapper.deleteByRoleId(roleId);
        
        // 添加新权限
        if (permIds != null && !permIds.isEmpty()) {
            for (Long permId : permIds) {
                SysRolePerm rolePerm = new SysRolePerm();
                rolePerm.setRoleId(roleId);
                rolePerm.setPermId(permId);
                rolePerm.setCreatedTime(LocalDateTime.now());
                sysRolePermMapper.insert(rolePerm);
            }
        }
        return true;
    }
    
    @Override
    public List<Long> getRolePermIds(Long roleId) {
        return sysRoleMapper.selectPermIdsByRoleId(roleId);
    }
    
    @Override
    public List<RoleDTO> listAllRoles() {
        List<SysRole> roles = this.list();
        return roles.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    private RoleDTO convertToDTO(SysRole role) {
        RoleDTO dto = new RoleDTO();
        BeanUtils.copyProperties(role, dto);
        
        // 查询角色权限ID列表
        List<Long> permIds = sysRoleMapper.selectPermIdsByRoleId(role.getId());
        dto.setPermissionIds(permIds);
        
        return dto;
    }
}
