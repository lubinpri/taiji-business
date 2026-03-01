package com.dataplatform.knowledge.controller;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {
    
    // 模拟知识库数据
    private final List<Map<String, Object>> knowledgeEntries = new ArrayList<>();
    
    // 模拟问答历史
    private final List<Map<String, Object>> chatHistory = new ArrayList<>();
    
    public KnowledgeController() {
        initKnowledgeBase();
    }
    
    private void initKnowledgeBase() {
        // 数据通识
        knowledgeEntries.add(Map.of(
            "id", 1, "title", "什么是数据要素",
            "content", "数据要素是指按照《数据安全法》规定，以电子或者其他方式对信息的记录。数据要素市场是数据流通交易的重要平台。",
            "type", "THEORY", "category", "数据通识", "views", 1256
        ));
        knowledgeEntries.add(Map.of(
            "id", 2, "title", "数据资产入表",
            "content", "数据资产入表是指将企业数据资源确认为资产负债表中的资产。",
            "type", "THEORY", "category", "数据通识", "views", 856
        ));
        
        // 数据技术
        knowledgeEntries.add(Map.of(
            "id", 3, "title", "数据清洗的常用方法",
            "content", "数据清洗包括：缺失值处理、异常值检测与处理、重复数据删除、数据格式标准化等。",
            "type", "PRACTICE", "category", "数据技术", "views", 1560
        ));
        knowledgeEntries.add(Map.of(
            "id", 4, "title", "ETL与ELT的区别",
            "content", "ETL是Extract-Transform-Load，ELT是Extract-Load-Transform。主要区别在于数据转换的时机。",
            "type", "PRACTICE", "category", "数据技术", "views", 642
        ));
        
        // 行业知识
        knowledgeEntries.add(Map.of(
            "id", 5, "title", "多式联运概念",
            "content", "多式联运是指由两种及其以上的交通工具相互衔接、转运而共同完成的运输过程。",
            "type", "CASE", "category", "交通运输", "views", 425
        ));
    }
    
    /**
     * RAG智能问答
     */
    @PostMapping("/chat")
    public Map<String, Object> chat(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        
        // 模拟RAG问答流程
        // 1. 意图识别
        // 2. 向量检索（模拟）
        // 3. 上下文组装
        // 4. 大模型生成（模拟）
        
        String answer = generateAnswer(question);
        List<Map<String, Object>> references = findReferences(question);
        
        // 保存问答历史
        Map<String, Object> chatRecord = new HashMap<>();
        chatRecord.put("id", chatHistory.size() + 1);
        chatRecord.put("question", question);
        chatRecord.put("answer", answer);
        chatRecord.put("timestamp", LocalDateTime.now().toString());
        chatHistory.add(chatRecord);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        
        Map<String, Object> data = new HashMap<>();
        data.put("answer", answer);
        data.put("references", references);
        response.put("data", data);
        
        return response;
    }
    
    private String generateAnswer(String question) {
        // 模拟基于知识库的答案生成
        if (question.contains("数据要素")) {
            return "数据要素是指按照《数据安全法》规定，以电子或者其他方式对信息的记录。数据要素市场是数据流通交易的重要平台。企业可以通过数据资产入表的方式，将数据资源转化为资产负债表中的资产。";
        } else if (question.contains("清洗")) {
            return "数据清洗主要包括以下方法：\n1. 缺失值处理：删除、均值填充、插值填充\n2. 异常值检测：3σ原则、箱线图法\n3. 重复数据删除\n4. 数据格式标准化";
        } else if (question.contains("联运")) {
            return "多式联运是指由两种及其以上的交通工具（如公路、铁路、水运、航空）相互衔接、转运而共同完成的运输过程。它能够充分发挥各种运输方式的优势，提高运输效率，降低物流成本。";
        }
        
        return "根据您的问题，我为您检索到以下相关信息：\n\n" +
               "1. 数据要素是数字化转型的基础资源\n" +
               "2. 数据资产入表是企业在数字化转型过程中的重要环节\n" +
               "3. 数据清洗是数据治理的重要步骤\n\n" +
               "如果您需要更详细的解答，请继续提问。";
    }
    
    private List<Map<String, Object>> findReferences(String question) {
        List<Map<String, Object>> refs = new ArrayList<>();
        
        for (Map<String, Object> entry : knowledgeEntries) {
            if (question.contains(entry.get("title").toString()) ||
                entry.get("content").toString().contains(question.substring(0, Math.min(4, question.length())))) {
                refs.add(Map.of(
                    "title", entry.get("title"),
                    "category", entry.get("category"),
                    "type", entry.get("type")
                ));
            }
        }
        
        // 确保至少有参考资料
        if (refs.isEmpty()) {
            refs.add(Map.of(
                "title", "数据要素概念",
                "category", "数据通识",
                "type", "THEORY"
            ));
        }
        
        return refs;
    }
    
    /**
     * 获取问答历史
     */
    @GetMapping("/chat/history")
    public Map<String, Object> getChatHistory() {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", chatHistory);
        
        return response;
    }
    
    /**
     * 知识库列表
     */
    @GetMapping("/entries")
    public Map<String, Object> listEntries(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type) {
        
        List<Map<String, Object>> result = knowledgeEntries;
        
        if (category != null) {
            result = result.stream()
                    .filter(e -> category.equals(e.get("category")))
                    .toList();
        }
        
        if (type != null) {
            result = result.stream()
                    .filter(e -> type.equals(e.get("type")))
                    .toList();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", result);
        
        return response;
    }
    
    /**
     * 知识详情
     */
    @GetMapping("/entries/{id}")
    public Map<String, Object> getEntry(@PathVariable Long id) {
        Map<String, Object> entry = knowledgeEntries.stream()
                .filter(e -> id.equals(e.get("id")))
                .findFirst()
                .orElse(null);
        
        // 增加浏览量
        if (entry != null) {
            entry.put("views", ((Number) entry.get("views")).intValue() + 1);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", entry);
        
        return response;
    }
    
    /**
     * 学习地图
     */
    @GetMapping("/learning-map")
    public Map<String, Object> getLearningMap() {
        Map<String, Object> learningMap = new HashMap<>();
        
        // 数据通识路径
        List<Map<String, Object>> dataPath = new ArrayList<>();
        dataPath.add(Map.of("id", 1, "name", "数据思维入门", "status", "COMPLETED"));
        dataPath.add(Map.of("id", 2, "name", "数据要素与数据资产", "status", "IN_PROGRESS"));
        dataPath.add(Map.of("id", 3, "name", "数据安全与合规", "status", "NOT_STARTED"));
        
        // 数据技术路径
        List<Map<String, Object>> techPath = new ArrayList<>();
        techPath.add(Map.of("id", 4, "name", "数据分析基础", "status", "COMPLETED"));
        techPath.add(Map.of("id", 5, "name", "Python数据处理", "status", "COMPLETED"));
        techPath.add(Map.of("id", 6, "name", "数据可视化", "status", "IN_PROGRESS"));
        techPath.add(Map.of("id", 7, "name", "数据建模", "status", "NOT_STARTED"));
        
        learningMap.put("dataPath", dataPath);
        learningMap.put("techPath", techPath);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", learningMap);
        
        return response;
    }
    
    /**
     * 统计信息
     */
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalEntries", knowledgeEntries.size());
        stats.put("totalChats", chatHistory.size());
        stats.put("todayChats", 56);
        
        // 分类统计
        Map<String, Integer> categoryStats = new HashMap<>();
        for (Map<String, Object> entry : knowledgeEntries) {
            String category = entry.get("category").toString();
            categoryStats.put(category, categoryStats.getOrDefault(category, 0) + 1);
        }
        stats.put("categoryStats", categoryStats);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", stats);
        
        return response;
    }
}
