package com.dnrush.service;

import com.dnrush.entity.ImageResource;
import com.dnrush.repository.ImageResourceRepository;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ImageService {
    
    @Autowired
    private ImageResourceRepository imageResourceRepository;
    
    @Value("${app.upload.path:uploads/}")
    private String uploadPath;
    
    @Value("${app.image.base64.prefix:data:image/jpeg;base64,}")
    private String base64Prefix;
    
    public List<ImageResource> getImagesByCategory(String category) {
        return imageResourceRepository.findActiveByCategory(category);
    }
    
    public List<ImageResource> getAllActiveImages() {
        return imageResourceRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }
    
    public ImageResource getImageById(Long id) {
        return imageResourceRepository.findById(id).orElse(null);
    }
    
    public ImageResource getImageByName(String name) {
        return imageResourceRepository.findByNameAndIsActiveTrue(name).orElse(null);
    }
    
    public ImageResource saveImage(MultipartFile file, String category, String description) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String uniqueName = UUID.randomUUID().toString() + fileExtension;
        
        // 創建上傳目錄
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        
        // 保存檔案
        Path filePath = uploadDir.resolve(uniqueName);
        Files.copy(file.getInputStream(), filePath);
        
        // 轉換為Base64
        String base64Data = convertToBase64(filePath.toFile());
        
        // 創建ImageResource實體
        ImageResource imageResource = new ImageResource();
        imageResource.setName(uniqueName);
        imageResource.setOriginalName(originalFilename);
        imageResource.setFilePath(filePath.toString());
        imageResource.setBase64Data(base64Data);
        imageResource.setMimeType(file.getContentType());
        imageResource.setFileSize(file.getSize());
        imageResource.setCategory(category);
        imageResource.setDescription(description);
        
        return imageResourceRepository.save(imageResource);
    }
    
    public String convertToBase64(File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] bytes = IOUtils.toByteArray(fileInputStream);
            return base64Prefix + Base64.encodeBase64String(bytes);
        }
    }
    
    public String convertToBase64(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        return base64Prefix + Base64.encodeBase64String(bytes);
    }
    
    public List<ImageResource> getImagesWithBase64Data() {
        return imageResourceRepository.findActiveWithBase64Data();
    }
    
    public ImageResource updateImage(Long id, MultipartFile file, String category, String description) throws IOException {
        ImageResource existingImage = getImageById(id);
        if (existingImage == null) {
            return null;
        }
        
        if (file != null && !file.isEmpty()) {
            // 刪除舊檔案
            if (existingImage.getFilePath() != null) {
                Files.deleteIfExists(Paths.get(existingImage.getFilePath()));
            }
            
            // 保存新檔案
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String uniqueName = UUID.randomUUID().toString() + fileExtension;
            
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            
            Path filePath = uploadDir.resolve(uniqueName);
            Files.copy(file.getInputStream(), filePath);
            
            // 更新Base64資料
            String base64Data = convertToBase64(filePath.toFile());
            
            existingImage.setName(uniqueName);
            existingImage.setOriginalName(originalFilename);
            existingImage.setFilePath(filePath.toString());
            existingImage.setBase64Data(base64Data);
            existingImage.setMimeType(file.getContentType());
            existingImage.setFileSize(file.getSize());
        }
        
        if (category != null) {
            existingImage.setCategory(category);
        }
        
        if (description != null) {
            existingImage.setDescription(description);
        }
        
        return imageResourceRepository.save(existingImage);
    }
    
    public void deleteImage(Long id) throws IOException {
        ImageResource image = getImageById(id);
        if (image != null) {
            // 刪除檔案
            if (image.getFilePath() != null) {
                Files.deleteIfExists(Paths.get(image.getFilePath()));
            }
            // 刪除資料庫記錄
            imageResourceRepository.deleteById(id);
        }
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex == -1 ? "" : filename.substring(lastDotIndex);
    }
}
