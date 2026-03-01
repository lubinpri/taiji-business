package com.dataplatform.talent.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataplatform.talent.model.TalentProfile;
import com.dataplatform.talent.service.TalentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/talent")
public class TalentController {
    
    @Autowired
    private TalentService talentService;
    
    /**
     * 获取人才列表
     */
    @GetMapping("/list")
    public Map<String, Object> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String location) {
        
        Map<String, Object> params = new HashMap<>();
        if (name != null) params.put("name", name);
        if (status != null) params.put("status", status);
        if (location != null) params.put("location", location);
        
        Page<TalentProfile> page = new Page<>(pageNum, pageSize);
        IPage<TalentProfile> result = talentService.listTalents(page, params);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", result.getRecords());
        response.put("total", result.getTotal());
        response.put("pages", result.getPages());
        
        return response;
    }
    
    /**
     * 获取人才详情
     */
    @GetMapping("/{id}")
    public Map<String, Object> getDetail(@PathVariable Long id) {
        TalentProfile talent = talentService.getTalentDetail(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", talent);
        
        return response;
    }
    
    /**
     * 获取人才画像
     */
    @GetMapping("/{id}/portrait")
    public Map<String, Object> getPortrait(@PathVariable Long id) {
        Map<String, Object> portrait = talentService.getTalentPortrait(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", portrait);
        
        return response;
    }
    
    /**
     * 获取人才标签
     */
    @GetMapping("/{id}/tags")
    public Map<String, Object> getTags(@PathVariable Long id) {
        List<Map<String, Object>> tags = talentService.getTalentTags(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", tags);
        
        return response;
    }
    
    /**
     * 添加人才标签
     */
    @PostMapping("/{id}/tags")
    public Map<String, Object> addTag(
            @PathVariable Long id,
            @RequestParam String tagType,
            @RequestParam String tagValue) {
        
        boolean success = talentService.addTalentTag(id, tagType, tagValue);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", success ? 200 : 500);
        response.put("message", success ? "success" : "failed");
        
        return response;
    }
    
    /**
     * 供需匹配
     */
    @GetMapping("/{id}/matches")
    public Map<String, Object> matchDemands(@PathVariable Long id) {
        List<Map<String, Object>> matches = talentService.matchDemands(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", matches);
        
        return response;
    }
    
    /**
     * 创建人才
     */
    @PostMapping
    public Map<String, Object> create(@RequestBody TalentProfile talent) {
        boolean success = talentService.createTalent(talent);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", success ? 200 : 500);
        response.put("message", success ? "创建成功" : "创建失败");
        response.put("data", talent);
        
        return response;
    }
    
    /**
     * 更新人才
     */
    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody TalentProfile talent) {
        talent.setId(id);
        boolean success = talentService.updateTalent(talent);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", success ? 200 : 500);
        response.put("message", success ? "更新成功" : "更新失败");
        
        return response;
    }
    
    /**
     * 删除人才
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        boolean success = talentService.deleteTalent(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", success ? 200 : 500);
        response.put("message", success ? "删除成功" : "删除失败");
        
        return response;
    }
    
    /**
     * 统计信息
     */
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalTalents", 1256);
        stats.put("inSchool", 320);
        stats.put("training", 180);
        stats.put("jobSeeking", 256);
        stats.put("certified", 500);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", stats);
        
        return response;
    }
}
