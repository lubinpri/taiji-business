package com.dbms.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dbms.permission.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 权限Mapper接口
 */
@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {
    
    /**
     * 根据权限编码查询权限
     */
    SysPermission selectByPermCode(@Param("permCode") String permCode);
    
    /**
     * 查询子权限列表
     */
    List<SysPermission> selectByParentId(@Param("parentId") Long parentId);
}
