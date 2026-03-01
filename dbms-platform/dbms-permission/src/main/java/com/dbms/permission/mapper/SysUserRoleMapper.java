package com.dbms.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dbms.permission.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户角色关联Mapper接口
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
    
    /**
     * 删除用户的所有角色关联
     */
    int deleteByUserId(@Param("userId") Long userId);
    
    /**
     * 删除角色的所有用户关联
     */
    int deleteByRoleId(@Param("roleId") Long roleId);
}
