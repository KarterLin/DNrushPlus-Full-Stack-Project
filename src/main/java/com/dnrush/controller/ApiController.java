package com.dnrush.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<ImageResource> getImageById(@PathVariable Long id) {
        ImageResource image = imageService.getImageById(id);
        if (image != null) {
            return ResponseEntity.ok(image);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/images/base64")
    public ResponseEntity<List<ImageResource>> getImagesWithBase64() {
        List<ImageResource> images = imageService.getImagesWithBase64Data();
        return ResponseEntity.ok(images);
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
}
