package com.dataplatform.talent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataplatform.talent.mapper.TalentProfileMapper;
import com.dataplatform.talent.model.TalentProfile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TalentServiceImpl extends ServiceImpl<TalentProfileMapper, TalentProfile> implements TalentService {
    
    @Override
    public IPage<TalentProfile> listTalents(Page<TalentProfile> page, Map<String, Object> params) {
        LambdaQueryWrapper<TalentProfile> wrapper = new LambdaQueryWrapper<>();
        
        if (params.containsKey("name") && StringUtils.hasText(params.get("name").toString())) {
            wrapper.like(TalentProfile::getName, params.get("name"));
        }
        if (params.containsKey("status") && StringUtils.hasText(params.get("status").toString())) {
            wrapper.eq(TalentProfile::getStatus, params.get("status"));
        }
        if (params.containsKey("location") && StringUtils.hasText(params.get("location").toString())) {
            wrapper.eq(TalentProfile::getLocation, params.get("location"));
        }
        
        wrapper.orderByDesc(TalentProfile::getCreateTime);
        return page(page, wrapper);
    }
    
    @Override
    public TalentProfile getTalentDetail(Long id) {
        return getById(id);
    }
    
    @Override
    public boolean createTalent(TalentProfile talent) {
        talent.setCreateTime(LocalDateTime.now());
        talent.setUpdateTime(LocalDateTime.now());
        talent.setStatus("IN_SCHOOL");
        talent.setOverallScore(0.0);
        talent.setAbilityScore(0.0);
        talent.setPotentialScore(0.0);
        talent.setCreditScore(0.0);
        return save(talent);
    }
    
    @Override
    public boolean updateTalent(TalentProfile talent) {
        talent.setUpdateTime(LocalDateTime.now());
        return updateById(talent);
    }
    
    @Override
    public boolean deleteTalent(Long id) {
        return removeById(id);
    }
    
    @Override
    public Map<String, Object> getTalentPortrait(Long id) {
        TalentProfile talent = getById(id);
        if (talent == null) {
            return Collections.emptyMap();
        }
        
        Map<String, Object> portrait = new HashMap<>();
        portrait.put("id", talent.getId());
        portrait.put("name", talent.getName());
        portrait.put("overallScore", talent.getOverallScore());
        
        // 能力维度
        Map<String, Object> ability = new HashMap<>();
        ability.put("score", talent.getAbilityScore());
        ability.put("dataAnalysis", 3.5 + Math.random() * 1.5);
        ability.put("dataGovernance", 3.0 + Math.random() * 2.0);
        ability.put("modeling", 3.0 + Math.random() * 2.0);
        ability.put("domainExpertise", 3.5 + Math.random() * 1.5);
        portrait.put("ability", ability);
        
        // 潜力维度
        Map<String, Object> potential = new HashMap<>();
        potential.put("score", talent.getPotentialScore());
        potential.put("learningSpeed", 3.5 + Math.random() * 1.5);
        potential.put("growthRate", 3.5 + Math.random() * 1.5);
        potential.put("adaptability", 4.0 + Math.random() * 1.0);
        portrait.put("potential", potential);
        
        // 信用维度
        Map<String, Object> credit = new HashMap<>();
        credit.put("score", talent.getCreditScore());
        credit.put("completedProjects", (int)(5 + Math.random() * 15));
        credit.put("rating", "A");
        credit.put("certifications", (int)(2 + Math.random() * 5));
        portrait.put("credit", credit);
        
        return portrait;
    }
    
    @Override
    public List<Map<String, Object>> getTalentTags(Long talentId) {
        // 模拟标签数据
        List<Map<String, Object>> tags = new ArrayList<>();
        
        // 技能标签
        tags.add(Map.of("id", 1, "type", "SKILL", "value", "数据分析", "weight", 0.9));
        tags.add(Map.of("id", 2, "type", "SKILL", "value", "Python", "weight", 0.85));
        tags.add(Map.of("id", 3, "type", "SKILL", "value", "数据建模", "weight", 0.8));
        
        // 行业标签
        tags.add(Map.of("id", 4, "type", "INDUSTRY", "value", "交通运输", "weight", 0.9));
        tags.add(Map.of("id", 5, "type", "INDUSTRY", "value", "智慧城市", "weight", 0.75));
        
        // 等级标签
        tags.add(Map.of("id", 6, "type", "LEVEL", "value", "中级", "weight", 0.8));
        
        return tags;
    }
    
    @Override
    public boolean addTalentTag(Long talentId, String tagType, String tagValue) {
        // 模拟添加标签
        return true;
    }
    
    @Override
    public boolean removeTalentTag(Long tagId) {
        return true;
    }
    
    @Override
    public List<Map<String, Object>> matchDemands(Long talentId) {
        // 模拟匹配结果
        List<Map<String, Object>> matches = new ArrayList<>();
        
        matches.add(Map.of(
            "id", 1,
            "companyName", "某科技公司",
            "position", "数据分析师",
            "matchScore", 0.85,
            "salaryMin", 15000,
            "salaryMax", 25000
        ));
        
        matches.add(Map.of(
            "id", 2,
            "companyName", "某数据公司",
            "position", "数据治理工程师",
            "matchScore", 0.78,
            "salaryMin", 18000,
            "salaryMax", 30000
        ));
        
        return matches;
    }
}
