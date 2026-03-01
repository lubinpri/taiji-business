package com.dataplatform.talent.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dataplatform.talent.model.TalentProfile;

import java.util.List;
import java.util.Map;

public interface TalentService extends IService<TalentProfile> {
    
    // 人才管理
    IPage<TalentProfile> listTalents(Page<TalentProfile> page, Map<String, Object> params);
    
    TalentProfile getTalentDetail(Long id);
    
    boolean createTalent(TalentProfile talent);
    
    boolean updateTalent(TalentProfile talent);
    
    boolean deleteTalent(Long id);
    
    // 画像管理
    Map<String, Object> getTalentPortrait(Long id);
    
    // 标签管理
    List<Map<String, Object>> getTalentTags(Long talentId);
    
    boolean addTalentTag(Long talentId, String tagType, String tagValue);
    
    boolean removeTalentTag(Long tagId);
    
    // 供需匹配
    List<Map<String, Object>> matchDemands(Long talentId);
}
