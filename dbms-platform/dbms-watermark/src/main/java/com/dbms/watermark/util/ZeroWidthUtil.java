package com.dbms.watermark.util;

/**
 * 零宽字符编码工具类
 * 用于将水印信息编码为不可见字符
 */
public class ZeroWidthUtil {
    
    // 零宽字符
    private static final char ZERO_WIDTH_SPACE = '\u200B';      // 零宽空格
    private static final char ZERO_WIDTH_NON_JOINER = '\u200C'; // 零宽非连接符
    private static final char ZERO_WIDTH_JOINER = '\u200D';     // 零宽连接符
    private static final char LEFT_TO_RIGHT_MARK = '\u200E';    // 左至右标记
    private static final char RIGHT_TO_LEFT_MARK = '\u200F';    // 右至左标记
    
    /**
     * 将字符串编码为零宽字符
     * @param text 原始文本
     * @return 编码后的零宽字符字符串
     */
    public static String encode(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        
        StringBuilder result = new StringBuilder();
        String binary = textToBinary(text);
        
        // 每2位二进制转换为1个零宽字符
        for (int i = 0; i < binary.length(); i += 2) {
            int bits = Integer.parseInt(binary.substring(i, Math.min(i + 2, binary.length())));
            switch (bits) {
                case 0:
                    result.append(ZERO_WIDTH_SPACE);
                    break;
                case 1:
                    result.append(ZERO_WIDTH_NON_JOINER);
                    break;
                case 2:
                    result.append(ZERO_WIDTH_JOINER);
                    break;
                case 3:
                    result.append(LEFT_TO_RIGHT_MARK);
                    break;
                default:
                    result.append(ZERO_WIDTH_SPACE);
            }
        }
        
        // 添加分隔符
        result.append(LEFT_TO_RIGHT_MARK).append(RIGHT_TO_LEFT_MARK);
        
        return result.toString();
    }
    
    /**
     * 从零宽字符解码出原始文本
     * @param encoded 编码后的字符串
     * @return 解码后的原始文本
     */
    public static String decode(String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            return "";
        }
        
        StringBuilder binary = new StringBuilder();
        
        for (char c : encoded.toCharArray()) {
            switch (c) {
                case ZERO_WIDTH_SPACE:
                    binary.append("00");
                    break;
                case ZERO_WIDTH_NON_JOINER:
                    binary.append("01");
                    break;
                case ZERO_WIDTH_JOINER:
                    binary.append("10");
                    break;
                case LEFT_TO_RIGHT_MARK:
                    // 分隔符，忽略
                    break;
                case RIGHT_TO_LEFT_MARK:
                    // 分隔符，忽略
                    break;
                default:
                    // 普通字符，忽略
                    break;
            }
        }
        
        return binaryToText(binary.toString());
    }
    
    /**
     * 文本转二进制
     */
    private static String textToBinary(String text) {
        StringBuilder binary = new StringBuilder();
        for (char c : text.toCharArray()) {
            String bin = Integer.toBinaryString(c);
            // 补齐到8位
            while (bin.length() < 8) {
                bin = "0" + bin;
            }
            binary.append(bin);
        }
        return binary.toString();
    }
    
    /**
     * 二进制转文本
     */
    private static String binaryToText(String binary) {
        if (binary.length() % 8 != 0) {
            return "";
        }
        
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < binary.length(); i += 8) {
            String byteStr = binary.substring(i, i + 8);
            int charCode = Integer.parseInt(byteStr, 2);
            text.append((char) charCode);
        }
        return text.toString();
    }
    
    /**
     * 检查字符串是否包含零宽字符
     */
    public static boolean containsZeroWidthChars(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        for (char c : text.toCharArray()) {
            if (c == ZERO_WIDTH_SPACE || c == ZERO_WIDTH_NON_JOINER || 
                c == ZERO_WIDTH_JOINER || c == LEFT_TO_RIGHT_MARK || c == RIGHT_TO_LEFT_MARK) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 移除字符串中的零宽字符
     */
    public static String removeZeroWidthChars(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.replaceAll("[\u200B-\u200F]", "");
    }
}
