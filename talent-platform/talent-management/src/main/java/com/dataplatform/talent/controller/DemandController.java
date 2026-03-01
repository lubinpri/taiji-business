package com.dataplatform.talent.controller;

import com.dataplatform.talent.model.EnterpriseDemand;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/demand")
public class DemandController {
    
    // 模拟数据存储
    private final List<EnterpriseDemand> demands = new ArrayList<>();
    
    public DemandController() {
        // 初始化模拟数据
        demands.add(createDemand("某科技公司", "互联网", "数据分析师", 15000, 25000, "北京"));
        demands.add(createDemand("某数据公司", "大数据", "数据治理工程师", 18000, 30000, "上海"));
        demands.add(createDemand("某智慧城市公司", "智慧城市", "数据产品经理", 20000, 35000, "深圳"));
        demands.add(createDemand("某交通公司", "交通运输", "算法工程师", 25000, 45000, "杭州"));
    }
    
    private EnterpriseDemand createDemand(String company, String industry, String position, 
            int salaryMin, int salaryMax, String location) {
        EnterpriseDemand demand = new EnterpriseDemand();
        demand.setId((long)(demands.size() + 1));
        demand.setCompanyName(company);
        demand.setIndustry(industry);
        demand.setPosition(position);
        demand.setSalaryMin(salaryMin);
        demand.setSalaryMax(salaryMax);
        demand.setLocation(location);
        demand.setQuantity(3);
        demand.setStatus("OPEN");
        demand.setCreateTime(LocalDateTime.now());
        return demand;
    }
    
    @GetMapping("/list")
    public Map<String, Object> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", demands);
        response.put("total", demands.size());
        
        return response;
    }
    
    @GetMapping("/{id}")
    public Map<String, Object> getDetail(@PathVariable Long id) {
        EnterpriseDemand demand = demands.stream()
                .filter(d -> d.getId().equals(id))
                .findFirst()
                .orElse(null);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", demand);
        
        return response;
    }
    
    @PostMapping
    public Map<String, Object> create(@RequestBody EnterpriseDemand demand) {
        demand.setId((long)(demands.size() + 1));
        demand.setCreateTime(LocalDateTime.now());
        demand.setStatus("OPEN");
        demands.add(demand);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "创建成功");
        response.put("data", demand);
        
        return response;
    }
    
    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody EnterpriseDemand demand) {
        demand.setId(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "更新成功");
        
        return response;
    }
    
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        demands.removeIf(d -> d.getId().equals(id));
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "删除成功");
        
        return response;
    }
}
