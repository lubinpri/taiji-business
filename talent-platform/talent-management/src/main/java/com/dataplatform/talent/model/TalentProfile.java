package com.dataplatform.talent.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("talent_profile")
public class TalentProfile {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String eId;                    // 电子证照ID
    private String name;                  // 姓名
    private String gender;                 // 性别
    private Integer age;                   // 年龄
    private String phone;                  // 手机号
    private String email;                  // 邮箱
    private String idCard;                 // 身份证号
    
    // 教育信息
    private String education;              // 学历
    private String major;                 // 专业
    private String university;             // 毕业院校
    
    // 工作信息
    private String company;                // 当前公司
    private String position;              // 当前职位
    private Integer workYears;            // 工作年限
    private String location;              // 所在城市
    
    // 画像维度
    private Double abilityScore;           // 能力评分
    private Double potentialScore;        // 潜力评分
    private Double creditScore;           // 信用评分
    private Double overallScore;          // 综合评分
    
    // 状态
    private String status;                // IN_SCHOOL/TRAINING/JOB_SEEKING/CERTIFIED
    private String source;               // 来源
    
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
