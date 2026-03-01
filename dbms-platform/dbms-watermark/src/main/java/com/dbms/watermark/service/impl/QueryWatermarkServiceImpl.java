package com.dbms.watermark.service.impl;

import com.dbms.watermark.config.WatermarkProperties;
import com.dbms.watermark.dto.WatermarkInfo;
import com.dbms.watermark.service.QueryWatermarkService;
import com.dbms.watermark.util.ZeroWidthUtil;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 查询结果水印服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QueryWatermarkServiceImpl implements QueryWatermarkService {
    
    private final WatermarkProperties watermarkProperties;
    
    @Override
    public String addInvisibleWatermark(String text, WatermarkInfo watermarkInfo) {
        if (!StringUtils.hasText(text)) {
            return text;
        }
        
        // 将水印信息编码为零宽字符
        String watermarkText = watermarkInfo.toShortText();
        String invisibleMark = ZeroWidthUtil.encode(watermarkText);
        
        // 追加到文本末尾
        return text + invisibleMark;
    }
    
    @Override
    public String addTextWatermark(String text, String watermarkText) {
        if (!StringUtils.hasText(text)) {
            return text;
        }
        
        // 在每行或每段落后添加水印
        StringBuilder result = new StringBuilder();
        String[] lines = text.split("\n", -1);
        
        for (int i = 0; i < lines.length; i++) {
            result.append(lines[i]);
            if (i % 3 == 0) {  // 每3行添加一次水印
                result.append(" | ").append(watermarkText);
            }
            if (i < lines.length - 1) {
                result.append("\n");
            }
        }
        
        return result.toString();
    }
    
    @Override
    public byte[] addExcelWatermark(byte[] excelData, WatermarkInfo watermarkInfo) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(excelData);
             XSSFWorkbook workbook = new XSSFWorkbook(bis);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            
            // 获取工作表
            var sheet = workbook.getSheetAt(0);
            
            // 创建水印样式
            var cellStyle = workbook.createCellStyle();
            var font = workbook.createFont();
            font.setFontHeightInPoints((short) watermarkProperties.getText().getFontSize());
            font.setColor(org.apache.poi.ss.usermodel.Color.GREY_50_PERCENT.index);
            cellStyle.setFont(font);
            
            // 在页眉添加水印（示例：在第一行添加水印行）
            var headerRow = sheet.createRow(0);
            var watermarkCell = headerRow.createCell(0);
            watermarkCell.setCellValue(watermarkInfo.toWatermarkText());
            watermarkCell.setCellStyle(cellStyle);
            
            // 设置行高
            headerRow.setHeight((short) 300);
            
            workbook.write(baos);
            return baos.toByteArray();
            
        } catch (Exception e) {
            log.error("添加Excel水印失败", e);
            throw new RuntimeException("添加Excel水印失败: " + e.getMessage());
        }
    }
    
    @Override
    public byte[] addPdfWatermark(byte[] pdfData, WatermarkInfo watermarkInfo) {
        // 使用iText 7处理PDF
        try (ByteArrayInputStream bis = new ByteArrayInputStream(pdfData);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            
            PdfReader reader = new PdfReader(bis);
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(reader, writer);
            
            int pageCount = pdfDoc.getNumberOfPages();
            
            // 生成水印文本
            String watermarkText = generatePdfWatermarkText(watermarkInfo);
            
            // 创建字体
            var font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            
            // 为每一页添加水印
            for (int i = 1; i <= pageCount; i++) {
                var page = pdfDoc.getPage(i);
                var pageSize = page.getPageSize();
                
                // 计算水印位置（45度角）
                float x = pageSize.getWidth() / 2;
                float y = pageSize.getHeight() / 2;
                
                // 添加水印段落
                var paragraph = new Paragraph(watermarkText)
                        .setFont(font)
                        .setFontSize(watermarkProperties.getPdf().getFontSize())
                        .setFontColor(ColorConstants.GRAY)
                        .setOpacity(watermarkProperties.getText().getAlpha());
                
                var canvas = pdfDoc.getPage(i).getCanvas();
                canvas.saveState();
                canvas.concatMatrix(com.itextpdf.kernel.geom.AffineTransform.getRotateInstance(
                        Math.toRadians(watermarkProperties.getPdf().getRotation()), x, y));
                canvas.openStream(); // 触发内容流写入
                
                var document = new Document(pdfDoc);
                document.showTextAligned(paragraph, x, y, i,
                        TextAlignment.CENTER, TextAlignment.MIDDLE, 0);
                
                canvas.restoreState();
            }
            
            pdfDoc.close();
            return baos.toByteArray();
            
        } catch (Exception e) {
            log.error("添加PDF水印失败（iText方式）", e);
            // 尝试使用PDFBox方式
            return addPdfWatermarkWithPdfBox(pdfData, watermarkInfo);
        }
    }
    
    /**
     * 使用PDFBox添加PDF水印（备选方案）
     */
    private byte[] addPdfWatermarkWithPdfBox(byte[] pdfData, WatermarkInfo watermarkInfo) {
        try (PDDocument document = PDDocument.load(pdfData);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            
            String watermarkText = generatePdfWatermarkText(watermarkInfo);
            
            for (PDPage page : document.getPages()) {
                PDRectangle mediaBox = page.getMediaBox();
                PDPageContentStream contentStream = new PDPageContentStream(document, page, 
                        PDPageContentStream.AppendMode.APPEND, true);
                
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, watermarkProperties.getPdf().getFontSize());
                contentStream.setNonStrokingColor(150, 150, 150);
                
                // 计算位置和旋转
                float x = mediaBox.getWidth() / 2;
                float y = mediaBox.getHeight() / 2;
                contentStream.newLineAtOffset(x, y);
                contentStream.showText(watermarkText);
                contentStream.endText();
                contentStream.close();
            }
            
            document.save(baos);
            return baos.toByteArray();
            
        } catch (IOException e) {
            log.error("添加PDF水印失败（PDFBox方式）", e);
            throw new RuntimeException("添加PDF水印失败: " + e.getMessage());
        }
    }
    
    @Override
    public String addJsonWatermark(String jsonData, WatermarkInfo watermarkInfo, String watermarkType) {
        if (!StringUtils.hasText(jsonData)) {
            return jsonData;
        }
        
        if ("INVISIBLE".equalsIgnoreCase(watermarkType)) {
            // 不可见水印：追加零宽字符编码
            return addInvisibleWatermark(jsonData, watermarkInfo);
        } else {
            // 可见水印：在JSON末尾添加水印字段
            String watermarkField = String.format(
                    ", \"_watermark\": \"%s\"", watermarkInfo.toWatermarkText());
            
            // 尝试在JSON末尾的}前插入
            if (jsonData.trim().endsWith("}")) {
                return jsonData.substring(0, jsonData.lastIndexOf("}")) + watermarkField + "}";
            }
            return jsonData + watermarkField;
        }
    }
    
    @Override
    public WatermarkInfo extractInvisibleWatermark(String text) {
        // 从文本中提取零宽字符并解码
        String encoded = ZeroWidthUtil.decode(text);
        
        if (!StringUtils.hasText(encoded)) {
            return null;
        }
        
        // 解析水印信息格式：userId|timestamp|sessionId
        String[] parts = encoded.split("\\|");
        if (parts.length >= 3) {
            WatermarkInfo info = new WatermarkInfo();
            info.setUserId(parts[0]);
            try {
                info.setTimestamp(Long.parseLong(parts[1]));
            } catch (NumberFormatException e) {
                // ignore
            }
            info.setSessionId(parts[2]);
            return info;
        }
        
        return null;
    }
    
    /**
     * 生成PDF水印文本
     */
    private String generatePdfWatermarkText(WatermarkInfo watermarkInfo) {
        String template = watermarkProperties.getPdf().getWatermarkText();
        return template
                .replace("{userId}", watermarkInfo.getUserId())
                .replace("{timestamp}", String.valueOf(watermarkInfo.getTimestamp()))
                .replace("{sessionId}", watermarkInfo.getSessionId());
    }
}
