package com.dnrush.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dnrush.entity.EventPhoto;
import com.dnrush.entity.ImageResource;
import com.dnrush.entity.NavigationItem;
import com.dnrush.entity.SiteContent;
import com.dnrush.entity.Statistic;
import com.dnrush.entity.TeamMember;
import com.dnrush.service.EventPhotoService;
import com.dnrush.service.ImageService;
import com.dnrush.service.NavigationService;
import com.dnrush.service.SiteContentService;
import com.dnrush.service.StatisticService;
import com.dnrush.service.TeamMemberService;

@RestController
@RequestMapping("/api")
public class ApiController {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);
    
    @Autowired
    private NavigationService navigationService;
    
    @Autowired
    private SiteContentService siteContentService;
    
    @Autowired
    private ImageService imageService;
    
    @Autowired
    private TeamMemberService teamMemberService;
    
    @Autowired
    private EventPhotoService eventPhotoService;
    
    @Autowired
    private StatisticService statisticService;
    
    // 導航欄API
    @GetMapping("/navigation")
    public ResponseEntity<List<NavigationItem>> getNavigation() {
        List<NavigationItem> navigationItems = navigationService.getActiveRootItems();
        return ResponseEntity.ok(navigationItems);
    }
    
    // 網站內容API
    @GetMapping("/content/{contentKey}")
    public ResponseEntity<Map<String, String>> getContent(@PathVariable String contentKey) {
        String content = siteContentService.getContentValue(contentKey);
        Map<String, String> response = new HashMap<>();
        response.put("content", content);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/content/section/{section}")
    public ResponseEntity<List<SiteContent>> getContentBySection(@PathVariable String section) {
        List<SiteContent> contents = siteContentService.getContentBySection(section);
        return ResponseEntity.ok(contents);
    }
    
    // 圖片API
    @GetMapping("/images/category/{category}")
    public ResponseEntity<List<ImageResource>> getImagesByCategory(@PathVariable String category) {
        List<ImageResource> images = imageService.getImagesByCategory(category);
        return ResponseEntity.ok(images);
    }
    
    @GetMapping("/images/{id}")
    public ResponseEntity<Map<String, Object>> getImageById(@PathVariable Long id) {
        try {
            logger.debug("Fetching image with id: {}", id);
            ImageResource image = imageService.getImageById(id);
            if (image != null) {
                logger.debug("Found image: {}", image.getOriginalName());
                // 只返回必要的數據，不包含 Base64 以避免響應過大
                Map<String, Object> response = new HashMap<>();
                response.put("id", image.getId());
                response.put("originalName", image.getOriginalName() != null ? image.getOriginalName() : "");
                response.put("category", image.getCategory() != null ? image.getCategory() : "");
                response.put("description", image.getDescription() != null ? image.getDescription() : "");
                response.put("year", image.getYear());
                response.put("mimeType", image.getMimeType() != null ? image.getMimeType() : "");
                response.put("fileSize", image.getFileSize() != null ? image.getFileSize().longValue() : 0L);
                response.put("createdAt", image.getCreatedAt() != null ? image.getCreatedAt().toString() : "");
                return ResponseEntity.ok(response);
            } else {
                logger.warn("Image not found with id: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error fetching image with id: " + id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    @GetMapping("/images/base64")
    public ResponseEntity<List<ImageResource>> getImagesWithBase64() {
        List<ImageResource> images = imageService.getImagesWithBase64Data();
        return ResponseEntity.ok(images);
    }
    
    @GetMapping("/images/base64/{id}")
    public ResponseEntity<Map<String, Object>> getImageBase64ById(@PathVariable Long id) {
        try {
            ImageResource image = imageService.getImageById(id);
            if (image != null && image.getBase64Data() != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("id", image.getId());
                response.put("base64Data", image.getBase64Data());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error fetching image base64 data with id: " + id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/images/category/{category}/year/{year}")
    public ResponseEntity<List<ImageResource>> getImagesByCategoryAndYear(
            @PathVariable String category, @PathVariable Integer year) {
        try {
            List<ImageResource> images = imageService.getImagesByCategoryAndYear(category, year);
            return ResponseEntity.ok(images);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    @GetMapping("/images/years")
    public ResponseEntity<List<Integer>> getImageYears() {
        try {
            List<Integer> years = imageService.getDistinctYears();
            return ResponseEntity.ok(years);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    // 團隊成員API
    @GetMapping("/team")
    public ResponseEntity<List<TeamMember>> getTeamMembers() {
        List<TeamMember> teamMembers = teamMemberService.getActiveMembers();
        return ResponseEntity.ok(teamMembers);
    }
    
    @GetMapping("/team/{id}")
    public ResponseEntity<TeamMember> getTeamMember(@PathVariable Long id) {
        TeamMember teamMember = teamMemberService.getMemberById(id);
        if (teamMember != null) {
            return ResponseEntity.ok(teamMember);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 隊聚活動照片API
    @GetMapping("/events")
    public ResponseEntity<List<EventPhoto>> getEventPhotos() {
        List<EventPhoto> eventPhotos = eventPhotoService.getActivePhotos();
        return ResponseEntity.ok(eventPhotos);
    }
    
    @GetMapping("/events/year/{year}")
    public ResponseEntity<List<EventPhoto>> getEventPhotosByYear(@PathVariable Integer year) {
        List<EventPhoto> eventPhotos = eventPhotoService.getPhotosByYear(year);
        return ResponseEntity.ok(eventPhotos);
    }
    
    @GetMapping("/events/years")
    public ResponseEntity<List<Integer>> getActiveYears() {
        List<Integer> years = eventPhotoService.getActiveYears();
        return ResponseEntity.ok(years);
    }
    
    // 統計數據API
    @GetMapping("/statistics")
    public ResponseEntity<List<Statistic>> getStatistics() {
        List<Statistic> statistics = statisticService.getActiveStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    @GetMapping("/statistics/{statKey}")
    public ResponseEntity<Map<String, String>> getStatisticByKey(@PathVariable String statKey) {
        String value = statisticService.getStatisticValue(statKey);
        Map<String, String> response = new HashMap<>();
        response.put("value", value);
        return ResponseEntity.ok(response);
    }
    
    // 網站資料API (整合所有資料)
    @GetMapping("/website-data")
    public ResponseEntity<Map<String, Object>> getWebsiteData() {
        Map<String, Object> websiteData = new HashMap<>();
        
        // 導航欄
        websiteData.put("navigationItems", navigationService.getAllActiveItems());
        
        // 網站內容
        websiteData.put("content", siteContentService.getAllActiveContent());
        
        // 統計數據
        websiteData.put("statistics", statisticService.getActiveStatistics());
        
        // 團隊成員
        websiteData.put("teamMembers", teamMemberService.getActiveMembers());
        
        // 隊聚活動照片
        websiteData.put("eventPhotos", eventPhotoService.getActivePhotos());
        websiteData.put("activeYears", eventPhotoService.getActiveYears());
        
        // 圖片資源
        websiteData.put("images", imageService.getAllActiveImages());
        
        return ResponseEntity.ok(websiteData);
    }
    
    @GetMapping("/images/event/more")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> loadMoreEventImages(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "3") int limit,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String year) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<ImageResource> images;
            
            // 請求比 limit 多 1 個來檢查是否還有更多數據
            int checkLimit = limit + 1;
            
            if (category != null && !category.trim().isEmpty()) {
                if (year != null && !year.trim().isEmpty()) {
                    images = imageService.getImagesByCategoryAndYearPaginated(category, year, offset, checkLimit);
                } else {
                    images = imageService.getImagesByCategoryPaginated(category, offset, checkLimit);
                }
            } else if (year != null && !year.trim().isEmpty()) {
                images = imageService.getImagesByYearPaginated(year, offset, checkLimit);
            } else {
                images = imageService.getAllImagesPaginated(offset, checkLimit);
            }
            
            // 檢查是否有更多數據
            boolean hasMore = images.size() > limit;
            
            // 只返回請求的數量
            if (hasMore) {
                images = images.subList(0, limit);
            }
            
            // 優化數據傳輸 - 使用更小的批次大小和完整數據
            List<Map<String, Object>> optimizedImages = new ArrayList<>();
            for (ImageResource image : images) {
                Map<String, Object> imageData = new HashMap<>();
                imageData.put("id", image.getId());
                imageData.put("name", image.getName());
                imageData.put("originalName", image.getOriginalName());
                imageData.put("description", image.getDescription());
                imageData.put("year", image.getYear());
                imageData.put("category", image.getCategory());
                
                // 保持完整的 Base64 數據，但使用更小的批次大小來避免傳輸問題
                imageData.put("base64Data", image.getBase64Data());
                
                optimizedImages.add(imageData);
            }
            
            response.put("images", optimizedImages);
            response.put("hasMore", hasMore);
            response.put("status", "success");
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "載入圖片時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
        
        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache")
                .header("Content-Type", "application/json; charset=utf-8")
                .body(response);
    }
}
