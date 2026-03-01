package com.dataplatform.talent.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("enterprise_demand")
public class EnterpriseDemand {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String companyName;            // 企业名称
    private String industry;              // 行业
    private String position;              // 招聘岗位
    private String requirements;          // 岗位要求
    private Integer salaryMin;           // 薪资下限
    private Integer salaryMax;           // 薪资上限
    private String location;             // 工作地点
    private Integer quantity;            // 招聘人数
    
    // 技能要求
    private String skillRequirements;    // 技能要求(JSON)
    private String experienceRequirement; // 经验要求
    private String educationRequirement;  // 学历要求
    
    private String contactName;          // 联系人
    private String contactPhone;         // 联系电话
    
    private String status;               // OPEN/CLOSED
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
