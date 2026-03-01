package com.dbms.permission.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dbms.permission.dto.UserDTO;
import com.dbms.permission.entity.SysRole;
import com.dbms.permission.entity.SysUser;
import com.dbms.permission.entity.SysUserRole;
import com.dbms.permission.mapper.SysRoleMapper;
import com.dbms.permission.mapper.SysUserMapper;
import com.dbms.permission.mapper.SysUserRoleMapper;
import com.dbms.permission.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户Service实现
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    private static final String DEFAULT_PASSWORD = "123456";
    
    @Override
    public UserDTO login(String username, String password) {
        SysUser user = baseMapper.selectByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getStatus() == 0) {
            throw new RuntimeException("用户已被禁用");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        return getUserById(user.getId());
    }
    
    @Override
    public UserDTO getUserByUsername(String username) {
        SysUser user = baseMapper.selectByUsername(username);
        if (user == null) {
            return null;
        }
        return convertToDTO(user);
    }
    
    @Override
    public UserDTO getUserById(Long id) {
        SysUser user = baseMapper.selectById(id);
        if (user == null) {
            return null;
        }
        UserDTO dto = convertToDTO(user);
        
        // 查询用户角色
        List<String> roleCodes = baseMapper.selectRoleCodesByUserId(id);
        dto.setRoleCodes(roleCodes);
        
        // 查询用户权限
        List<String> permissions = baseMapper.selectPermCodesByUserId(id);
        dto.setPermissions(permissions);
        
        return dto;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createUser(UserDTO userDTO) {
        // 检查用户名是否存在
        SysUser existingUser = baseMapper.selectByUsername(userDTO.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        SysUser user = new SysUser();
        BeanUtils.copyProperties(userDTO, user);
        
        // 加密密码
        String encodedPassword = passwordEncoder.encode(
            StrUtil.isNotEmpty(userDTO.getPassword()) ? userDTO.getPassword() : DEFAULT_PASSWORD
        );
        user.setPassword(encodedPassword);
        
        boolean result = this.save(user);
        
        // 分配角色
        if (result && userDTO.getRoleIds() != null && !userDTO.getRoleIds().isEmpty()) {
            assignRoles(user.getId(), userDTO.getRoleIds());
        }
        
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(UserDTO userDTO) {
        if (userDTO.getId() == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        
        SysUser user = baseMapper.selectById(userDTO.getId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 如果修改了用户名，检查是否重复
        if (!user.getUsername().equals(userDTO.getUsername())) {
            SysUser existingUser = baseMapper.selectByUsername(userDTO.getUsername());
            if (existingUser != null) {
                throw new RuntimeException("用户名已存在");
            }
        }
        
        BeanUtils.copyProperties(userDTO, user, "id", "password", "createdBy", "createdTime");
        
        // 如果提供了新密码，则更新密码
        if (StrUtil.isNotEmpty(userDTO.getPassword())) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        
        boolean result = this.updateById(user);
        
        // 更新角色
        if (result && userDTO.getRoleIds() != null) {
            assignRoles(userDTO.getId(), userDTO.getRoleIds());
        }
        
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(Long id) {
        return this.removeById(id);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRoles(Long userId, List<Long> roleIds) {
        // 删除用户现有角色
        sysUserRoleMapper.deleteByUserId(userId);
        
        // 添加新角色
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRole.setCreatedTime(LocalDateTime.now());
                sysUserRoleMapper.insert(userRole);
            }
        }
        return true;
    }
    
    @Override
    public List<Long> getUserRoleIds(Long userId) {
        return sysRoleMapper.selectRoleIdsByUserId(userId);
    }
    
    @Override
    public List<String> getUserPermissions(Long userId) {
        return baseMapper.selectPermCodesByUserId(userId);
    }
    
    @Override
    public boolean hasPermission(Long userId, String permission) {
        List<String> permissions = getUserPermissions(userId);
        return permissions.contains(permission);
    }
    
    @Override
    public boolean hasRole(Long userId, String roleCode) {
        List<String> roleCodes = baseMapper.selectRoleCodesByUserId(userId);
        return roleCodes.contains(roleCode);
    }
    
    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = baseMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        return this.updateById(user);
    }
    
    @Override
    public boolean resetPassword(Long userId, String newPassword) {
        SysUser user = baseMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        return this.updateById(user);
    }
    
    private UserDTO convertToDTO(SysUser user) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        
        // 查询角色ID列表
        List<Long> roleIds = sysRoleMapper.selectRoleIdsByUserId(user.getId());
        dto.setRoleIds(roleIds);
        
        return dto;
    }
}
