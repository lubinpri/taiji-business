package com.dbms.mask.controller;

import com.dbms.common.dto.Result;
import com.dbms.mask.dto.BatchMaskRequest;
import com.dbms.mask.dto.MaskRequest;
import com.dbms.mask.service.DataMaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 数据脱敏控制器
 */
@RestController
@RequestMapping("/api/mask")
public class DataMaskController {
    
    @Autowired
    private DataMaskService dataMaskService;
    
    /**
     * 单条数据脱敏
     */
    @PostMapping("/data")
    public Result<String> maskData(@RequestBody MaskRequest request) {
        String masked = dataMaskService.mask(
                request.getData(),
                request.getDataType(),
                request.getAlgorithm(),
                request.getConfig()
        );
        return Result.success(masked);
    }
    
    /**
     * 批量数据脱敏
     */
    @PostMapping("/batch")
    public Result<List<String>> batchMask(@RequestBody BatchMaskRequest request) {
        List<String> maskedList = dataMaskService.batchMask(
                request.getDataList(),
                request.getDataType(),
                request.getAlgorithm(),
                request.getConfig()
        );
        return Result.success(maskedList);
    }
    
    /**
     * 使用规则脱敏数据
     */
    @PostMapping("/data/rule/{ruleId}")
    public Result<String> maskWithRule(@PathVariable Long ruleId, @RequestBody Map<String, String> body) {
        String data = body.get("data");
        String masked = dataMaskService.maskWithRule(data, ruleId);
        return Result.success(masked);
    }
    
    /**
     * 自动脱敏（根据数据类型）
     */
    @PostMapping("/auto")
    public Result<String> autoMask(@RequestBody Map<String, String> body) {
        String data = body.get("data");
        String dataType = body.get("dataType");
        String masked = dataMaskService.autoMask(data, dataType);
        return Result.success(masked);
    }
    
    /**
     * 获取支持的数据类型
     */
    @GetMapping("/data-types")
    public Result<List<String>> getDataTypes() {
        return Result.success(List.of(
                "idcard",      // 身份证
                "phone",       // 手机号
                "bank_card",   // 银行卡
                "email",       // 邮箱
                "name",        // 姓名
                "address"      // 地址
        ));
    }
    
    /**
     * 获取支持的算法类型
     */
    @GetMapping("/algorithms")
    public Result<List<String>> getAlgorithms() {
        return Result.success(List.of(
                "mask",     // 掩码算法
                "replace",  // 替换算法
                "encrypt",  // 加密算法
                "hash"      // 哈希算法
        ));
    }
}
