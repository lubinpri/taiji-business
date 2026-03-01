package com.dbms.watermark.service;

import com.dbms.watermark.dto.WatermarkInfo;

/**
 * 查询结果水印服务接口
 */
public interface QueryWatermarkService {
    
    /**
     * 为文本添加不可见水印（零宽字符编码）
     * @param text 原始文本
     * @param watermarkInfo 水印信息
     * @return 带水印的文本
     */
    String addInvisibleWatermark(String text, WatermarkInfo watermarkInfo);
    
    /**
     * 为文本添加可见文字水印
     * @param text 原始文本
     * @param watermarkText 水印文本
     * @return 带水印的文本
     */
    String addTextWatermark(String text, String watermarkText);
    
    /**
     * 为Excel添加水印
     * @param excelData Excel数据（字节数组）
     * @param watermarkInfo 水印信息
     * @return 带水印的Excel数据
     */
    byte[] addExcelWatermark(byte[] excelData, WatermarkInfo watermarkInfo);
    
    /**
     * 为PDF添加水印
     * @param pdfData PDF数据（字节数组）
     * @param watermarkInfo 水印信息
     * @return 带水印的PDF数据
     */
    byte[] addPdfWatermark(byte[] pdfData, WatermarkInfo watermarkInfo);
    
    /**
     * 为JSON数据添加水印
     * @param jsonData JSON字符串
     * @param watermarkInfo 水印信息
     * @param watermarkType 水印类型：TEXT, INFINITE
     * @return 带水印的JSON数据
     */
    String addJsonWatermark(String jsonData, WatermarkInfo watermarkInfo, String watermarkType);
    
    /**
     * 从文本中提取不可见水印信息
     * @param text 带水印的文本
     * @return 水印信息
     */
    WatermarkInfo extractInvisibleWatermark(String text);
}
