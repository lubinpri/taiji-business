package com.dbms.audit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dbms.common.entity.QueryLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QueryLogMapper extends BaseMapper<QueryLog> {
}
