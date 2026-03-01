package com.dbms.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dbms.permission.entity.DbAccessPolicy;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 数据库访问策略Mapper接口
 */
@Mapper
public interface DbAccessPolicyMapper extends BaseMapper<DbAccessPolicy> {
    
    /**
     * 查询用户的所有访问策略
     */
    List<DbAccessPolicy> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 查询用户在指定实例上的访问策略
     */
    List<DbAccessPolicy> selectByUserIdAndInstanceId(@Param("userId") Long userId, @Param("instanceId") Long instanceId);
    
    /**
     * 查询用户在指定库上的访问策略
     */
    List<DbAccessPolicy> selectByUserIdAndCatalog(@Param("userId") Long userId, @Param("catalogName") String catalogName);
}
