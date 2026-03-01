package com.dbms.mask.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dbms.mask.entity.MaskRule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 脱敏规则Mapper
 */
@Mapper
public interface MaskRuleMapper extends BaseMapper<MaskRule> {
}
