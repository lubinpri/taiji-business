package com.dataplatform.training.controller;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/training")
public class TrainingController {
    
    // 模拟课程数据
    private final List<Map<String, Object>> courses = new ArrayList<>();
    
    // 模拟实训任务数据
    private final List<Map<String, Object>> tasks = new ArrayList<>();
    
    // 模拟竞赛数据
    private final List<Map<String, Object>> contests = new ArrayList<>();
    
    public TrainingController() {
        initCourses();
        initTasks();
        initContests();
    }
    
    private void initCourses() {
        courses.add(Map.of(
            "id", 1, "title", "数据分析基础", "category", "数据技术",
            "level", "初级", "duration", 20, "students", 1256,
            "status", "PUBLISHED", "type", "VIDEO"
        ));
        courses.add(Map.of(
            "id", 2, "title", "Python数据处理", "category", "数据技术",
            "level", "中级", "duration", 30, "students", 856,
            "status", "PUBLISHED", "type", "VIDEO"
        ));
        courses.add(Map.of(
            "id", 3, "title", "数据可视化实战", "category", "数据技术",
            "level", "中级", "duration", 25, "students", 642,
            "status", "PUBLISHED", "type", "PRACTICE"
        ));
        courses.add(Map.of(
            "id", 4, "title", "数据思维入门", "category", "数据通识",
            "level", "初级", "duration", 10, "students", 2560,
            "status", "PUBLISHED", "type", "VIDEO"
        ));
    }
    
    private void initTasks() {
        tasks.add(Map.of(
            "id", 1, "name", "多式联运数据分析", "difficulty", "MEDIUM",
            "duration", 8, "status", "PUBLISHED",
            "description", "基于某物流公司运输数据，完成多式联运效率分析"
        ));
        tasks.add(Map.of(
            "id", 2, "name", "城市交通流量预测", "difficulty", "HARD",
            "duration", 24, "status", "PUBLISHED",
            "description", "利用历史交通数据，预测未来24小时城市交通流量"
        ));
        tasks.add(Map.of(
            "id", 3, "name", "医疗数据清洗实战", "difficulty", "EASY",
            "duration", 4, "status", "PUBLISHED",
            "description", "对某医院HIS系统导出的数据进行清洗和标准化"
        ));
    }
    
    private void initContests() {
        contests.add(Map.of(
            "id", 1, "name", "数据建模大赛", "status", "ONGOING",
            "participants", 256, "prize", "一等奖5000元",
            "endTime", "2026-03-15"
        ));
        contests.add(Map.of(
            "id", 2, "name", "创新方案大赛", "status", "UPCOMING",
            "participants", 0, "prize", "一等奖8000元",
            "endTime", "2026-04-01"
        ));
    }
    
    // ===== 课程相关 =====
    
    @GetMapping("/courses")
    public Map<String, Object> listCourses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String level) {
        
        List<Map<String, Object>> result = courses;
        if (category != null) {
            result = result.stream()
                    .filter(c -> category.equals(c.get("category")))
                    .toList();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", result);
        
        return response;
    }
    
    @GetMapping("/courses/{id}")
    public Map<String, Object> getCourse(@PathVariable Long id) {
        Map<String, Object> course = courses.stream()
                .filter(c -> id.equals(c.get("id")))
                .findFirst()
                .orElse(null);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", course);
        
        return response;
    }
    
    // ===== 实训相关 =====
    
    @GetMapping("/tasks")
    public Map<String, Object> listTasks() {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", tasks);
        
        return response;
    }
    
    @GetMapping("/tasks/{id}")
    public Map<String, Object> getTask(@PathVariable Long id) {
        Map<String, Object> task = tasks.stream()
                .filter(t -> id.equals(t.get("id")))
                .findFirst()
                .orElse(null);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", task);
        
        return response;
    }
    
    @PostMapping("/tasks/{id}/submit")
    public Map<String, Object> submitTask(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "提交成功");
        
        return response;
    }
    
    // ===== 竞赛相关 =====
    
    @GetMapping("/contests")
    public Map<String, Object> listContests() {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", contests);
        
        return response;
    }
    
    @GetMapping("/contests/{id}")
    public Map<String, Object> getContest(@PathVariable Long id) {
        Map<String, Object> contest = contests.stream()
                .filter(c -> id.equals(c.get("id")))
                .findFirst()
                .orElse(null);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", contest);
        
        return response;
    }
    
    @PostMapping("/contests/{id}/register")
    public Map<String, Object> registerContest(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "报名成功");
        
        return response;
    }
    
    // ===== 统计 =====
    
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalCourses", courses.size());
        stats.put("totalTasks", tasks.size());
        stats.put("totalContests", contests.size());
        stats.put("totalStudents", 3652);
        stats.put("completedTrainings", 1256);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", stats);
        
        return response;
    }
}
