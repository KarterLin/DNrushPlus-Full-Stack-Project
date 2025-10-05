package com.dnrush.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ImageUtils {
    
    /**
     * 將檔案轉換為Base64字串
     * @param file 要轉換的檔案
     * @return Base64編碼的字串
     * @throws IOException 檔案讀取錯誤
     */
    public static String convertFileToBase64(File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] bytes = IOUtils.toByteArray(fileInputStream);
            return Base64.encodeBase64String(bytes);
        }
    }
    
    /**
     * 將檔案轉換為Base64字串，包含MIME類型前綴
     * @param file 要轉換的檔案
     * @param mimeType MIME類型
     * @return 包含MIME類型前綴的Base64編碼字串
     * @throws IOException 檔案讀取錯誤
     */
    public static String convertFileToBase64WithMimeType(File file, String mimeType) throws IOException {
        String base64 = convertFileToBase64(file);
        return "data:" + mimeType + ";base64," + base64;
    }
    
    /**
     * 將位元組陣列轉換為Base64字串
     * @param bytes 位元組陣列
     * @return Base64編碼的字串
     */
    public static String convertBytesToBase64(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }
    
    /**
     * 將位元組陣列轉換為Base64字串，包含MIME類型前綴
     * @param bytes 位元組陣列
     * @param mimeType MIME類型
     * @return 包含MIME類型前綴的Base64編碼字串
     */
    public static String convertBytesToBase64WithMimeType(byte[] bytes, String mimeType) {
        String base64 = convertBytesToBase64(bytes);
        return "data:" + mimeType + ";base64," + base64;
    }
    
    /**
     * 將Base64字串轉換為位元組陣列
     * @param base64String Base64編碼的字串
     * @return 位元組陣列
     */
    public static byte[] convertBase64ToBytes(String base64String) {
        // 移除MIME類型前綴（如果存在）
        if (base64String.contains(",")) {
            base64String = base64String.substring(base64String.indexOf(",") + 1);
        }
        return Base64.decodeBase64(base64String);
    }
    
    /**
     * 將Base64字串保存為檔案
     * @param base64String Base64編碼的字串
     * @param outputPath 輸出檔案路徑
     * @throws IOException 檔案寫入錯誤
     */
    public static void saveBase64ToFile(String base64String, String outputPath) throws IOException {
        byte[] bytes = convertBase64ToBytes(base64String);
        Path path = Paths.get(outputPath);
        Files.write(path, bytes);
    }
    
    /**
     * 將Base64字串保存為檔案
     * @param base64String Base64編碼的字串
     * @param outputFile 輸出檔案
     * @throws IOException 檔案寫入錯誤
     */
    public static void saveBase64ToFile(String base64String, File outputFile) throws IOException {
        byte[] bytes = convertBase64ToBytes(base64String);
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
            fileOutputStream.write(bytes);
        }
    }
    
    /**
     * 檢查字串是否為有效的Base64格式
     * @param base64String 要檢查的字串
     * @return 是否為有效的Base64格式
     */
    public static boolean isValidBase64(String base64String) {
        if (base64String == null || base64String.isEmpty()) {
            return false;
        }
        
        // 移除MIME類型前綴（如果存在）
        if (base64String.contains(",")) {
            base64String = base64String.substring(base64String.indexOf(",") + 1);
        }
        
        try {
            Base64.decodeBase64(base64String);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 從Base64字串中提取MIME類型
     * @param base64String Base64編碼的字串
     * @return MIME類型，如果沒有則返回null
     */
    public static String extractMimeType(String base64String) {
        if (base64String == null || !base64String.startsWith("data:")) {
            return null;
        }
        
        int commaIndex = base64String.indexOf(",");
        if (commaIndex == -1) {
            return null;
        }
        
        String mimePart = base64String.substring(5, commaIndex); // 移除 "data:" 前綴
        int semicolonIndex = mimePart.indexOf(";");
        if (semicolonIndex != -1) {
            return mimePart.substring(0, semicolonIndex);
        }
        
        return mimePart;
    }
    
    /**
     * 壓縮Base64圖片（簡單的品質調整）
     * @param base64String 原始Base64字串
     * @param quality 品質 (0.0 - 1.0)
     * @return 壓縮後的Base64字串
     */
    public static String compressBase64Image(String base64String, float quality) {
        // 這裡可以實作圖片壓縮邏輯
        // 目前返回原始字串，實際專案中可以使用Java的圖片處理庫
        return base64String;
    }
    
    /**
     * 調整Base64圖片大小
     * @param base64String 原始Base64字串
     * @param maxWidth 最大寬度
     * @param maxHeight 最大高度
     * @return 調整大小後的Base64字串
     */
    public static String resizeBase64Image(String base64String, int maxWidth, int maxHeight) {
        // 這裡可以實作圖片大小調整邏輯
        // 目前返回原始字串，實際專案中可以使用Java的圖片處理庫
        return base64String;
    }
}
