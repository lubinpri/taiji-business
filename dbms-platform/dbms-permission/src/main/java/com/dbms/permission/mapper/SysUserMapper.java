package com.dbms.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dbms.permission.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    
    /**
     * 根据用户名查询用户
     */
    SysUser selectByUsername(@Param("username") String username);
    
    /**
     * 查询用户角色列表
     */
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);
    
    /**
     * 查询用户权限列表
     */
    List<String> selectPermCodesByUserId(@Param("userId") Long userId);
}
