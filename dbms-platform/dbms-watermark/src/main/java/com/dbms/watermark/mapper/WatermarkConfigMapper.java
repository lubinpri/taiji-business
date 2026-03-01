package com.dbms.watermark.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dbms.watermark.entity.WatermarkConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 水印配置Mapper
 */
@Mapper
public interface WatermarkConfigMapper extends BaseMapper<WatermarkConfig> {
}
