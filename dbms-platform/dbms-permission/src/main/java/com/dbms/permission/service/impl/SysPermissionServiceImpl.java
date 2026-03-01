package com.dbms.permission.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dbms.permission.dto.PermissionDTO;
import com.dbms.permission.entity.SysPermission;
import com.dbms.permission.mapper.SysPermissionMapper;
import com.dbms.permission.service.SysPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限Service实现
 */
@Service
@RequiredArgsConstructor
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {
    
    @Override
    public PermissionDTO getPermByCode(String permCode) {
        SysPermission perm = baseMapper.selectByPermCode(permCode);
        if (perm == null) {
            return null;
        }
        return convertToDTO(perm);
    }
    
    @Override
    public PermissionDTO getPermById(Long id) {
        SysPermission perm = baseMapper.selectById(id);
        if (perm == null) {
            return null;
        }
        return convertToDTO(perm);
    }
    
    @Override
    public boolean createPerm(PermissionDTO permissionDTO) {
        // 检查权限编码是否存在
        SysPermission existingPerm = baseMapper.selectByPermCode(permissionDTO.getPermCode());
        if (existingPerm != null) {
            throw new RuntimeException("权限编码已存在");
        }
        
        SysPermission perm = new SysPermission();
        BeanUtils.copyProperties(permissionDTO, perm);
        
        return this.save(perm);
    }
    
    @Override
    public boolean updatePerm(PermissionDTO permissionDTO) {
        if (permissionDTO.getId() == null) {
            throw new RuntimeException("权限ID不能为空");
        }
        
        SysPermission perm = baseMapper.selectById(permissionDTO.getId());
        if (perm == null) {
            throw new RuntimeException("权限不存在");
        }
        
        // 如果修改了权限编码，检查是否重复
        if (!perm.getPermCode().equals(permissionDTO.getPermCode())) {
            SysPermission existingPerm = baseMapper.selectByPermCode(permissionDTO.getPermCode());
            if (existingPerm != null) {
                throw new RuntimeException("权限编码已存在");
            }
        }
        
        BeanUtils.copyProperties(permissionDTO, perm, "id", "createdBy", "createdTime");
        
        return this.updateById(perm);
    }
    
    @Override
    public boolean deletePerm(Long id) {
        // 检查是否有子权限
        List<SysPermission> children = baseMapper.selectByParentId(id);
        if (children != null && !children.isEmpty()) {
            throw new RuntimeException("请先删除子权限");
        }
        
        return this.removeById(id);
    }
    
    @Override
    public List<PermissionDTO> listAllPermissionsTree() {
        // 查询所有权限
        List<SysPermission> allPerms = this.list();
        
        // 构建树形结构
        List<PermissionDTO> rootPerms = new ArrayList<>();
        
        for (SysPermission perm : allPerms) {
            if (perm.getParentId() == null || perm.getParentId() == 0) {
                PermissionDTO dto = convertToDTO(perm);
                buildChildren(dto, allPerms);
                rootPerms.add(dto);
            }
        }
        
        return rootPerms;
    }
    
    @Override
    public List<PermissionDTO> getChildPermissions(Long parentId) {
        List<SysPermission> children = baseMapper.selectByParentId(parentId);
        return children.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    private void buildChildren(PermissionDTO parent, List<SysPermission> allPerms) {
        List<PermissionDTO> children = new ArrayList<>();
        
        for (SysPermission perm : allPerms) {
            if (perm.getParentId() != null && perm.getParentId().equals(parent.getId())) {
                PermissionDTO child = convertToDTO(perm);
                buildChildren(child, allPerms);
                children.add(child);
            }
        }
        
        if (!children.isEmpty()) {
            parent.setChildren(children);
        }
    }
    
    private PermissionDTO convertToDTO(SysPermission perm) {
        PermissionDTO dto = new PermissionDTO();
        BeanUtils.copyProperties(perm, dto);
        return dto;
    }
}
