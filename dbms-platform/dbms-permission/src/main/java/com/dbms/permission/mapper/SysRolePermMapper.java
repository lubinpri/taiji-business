package com.dbms.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dbms.permission.entity.SysRolePerm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 角色权限关联Mapper接口
 */
@Mapper
public interface SysRolePermMapper extends BaseMapper<SysRolePerm> {
    
    /**
     * 删除角色的所有权限关联
     */
    int deleteByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 删除权限的所有角色关联
     */
    int deleteByPermId(@Param("permId") Long permId);
}
