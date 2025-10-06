package com.dnrush.service;

import com.dnrush.entity.ImageResource;
import com.dnrush.repository.ImageResourceRepository;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
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
        return saveImage(file, category, description, null);
    }
    
    public ImageResource saveImage(MultipartFile file, String category, String description, Integer year) throws IOException {
        // 檢查首頁橫幅的唯一性
        if ("hero".equals(category)) {
            List<ImageResource> existingHeroImages = imageResourceRepository.findActiveByCategory("hero");
            if (!existingHeroImages.isEmpty()) {
                // 刪除現有的首頁橫幅圖片
                for (ImageResource existingImage : existingHeroImages) {
                    deleteImage(existingImage.getId());
                }
            }
        }
        
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
        imageResource.setYear(year);
        
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
    
    public List<Integer> getDistinctYears() {
        return imageResourceRepository.findDistinctYears();
    }
    
    public boolean canDeleteYear(Integer year) {
        List<ImageResource> imagesWithYear = imageResourceRepository.findActiveByCategoryAndYear("event", year);
        return imagesWithYear.isEmpty();
    }
    
    public void deleteYear(Integer year) {
        // 年份本身不需要從資料庫中刪除，只要沒有照片使用該年份即可
        // 這個方法主要是為了一致性，實際上年份會在沒有相關照片時自動消失
    }
    
    public List<ImageResource> getImagesByCategoryAndYear(String category, Integer year) {
        if (year == null) {
            return imageResourceRepository.findActiveByCategory(category);
        }
        return imageResourceRepository.findActiveByCategoryAndYear(category, year);
    }
    
    public ImageResource updateImage(Long id, MultipartFile file, String category, String description) throws IOException {
        return updateImage(id, file, category, description, null);
    }
    
    public ImageResource updateImage(Long id, MultipartFile file, String category, String description, Integer year) throws IOException {
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
        
        if (year != null) {
            existingImage.setYear(year);
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
    
    // 分頁方法
    public List<ImageResource> getAllImagesPaginated(int offset, int limit) {
        return imageResourceRepository.findByStatusOrderByYearDescCreatedAtDescWithPagination(true, offset, limit);
    }
    
    public List<ImageResource> getImagesByCategoryPaginated(String category, int offset, int limit) {
        return imageResourceRepository.findByCategoryAndStatusOrderByYearDescCreatedAtDescWithPagination(category, true, offset, limit);
    }
    
    public List<ImageResource> getImagesByYearPaginated(String year, int offset, int limit) {
        return imageResourceRepository.findByYearAndStatusOrderByCreatedAtDescWithPagination(year, true, offset, limit);
    }
    
    public List<ImageResource> getImagesByCategoryAndYearPaginated(String category, String year, int offset, int limit) {
        return imageResourceRepository.findByCategoryAndYearAndStatusOrderByCreatedAtDescWithPagination(category, year, true, offset, limit);
    }
    
    // 專門為 TeamMemberService 提供的 ImageResource 保存方法
    public ImageResource saveImageResource(ImageResource imageResource) {
        return imageResourceRepository.save(imageResource);
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex == -1 ? "" : filename.substring(lastDotIndex);
    }
    
    /**
     * 獲取活躍圖片數量
     */
    public long getActiveCount() {
        return imageResourceRepository.findByIsActiveTrueOrderByCreatedAtDesc().size();
    }
}
