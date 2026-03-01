package com.dataplatform.talent.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("talent_skill_tag")
public class TalentSkillTag {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long talentId;                // 人才ID
    private String tagType;               // 标签类型: SKILL/INDUSTRY/LEVEL/REGION
    private String tagValue;              // 标签值
    private Double weight;                 // 权重
    
    private LocalDateTime createTime;
}
