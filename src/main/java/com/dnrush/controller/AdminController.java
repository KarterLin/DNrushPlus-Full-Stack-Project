package com.dnrush.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

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

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
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
    
    // 管理首頁
    @GetMapping
    public String adminIndex(Model model) {
        return "admin/index";
    }
    
    // 導航欄管理
    @GetMapping("/navigation")
    public String navigationManagement(Model model) {
        try {
            List<NavigationItem> allItems = navigationService.getAllItems();
            model.addAttribute("navigationItems", allItems);
            return "admin/navigation";
        } catch (Exception e) {
            logger.error("加載導航欄項目時出錯", e);
            model.addAttribute("error", "加載導航欄項目時出錯: " + e.getMessage());
            return "error";
        }
    }
    
    @PostMapping("/navigation")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveNavigation(@RequestBody Map<String, Object> requestBody) {
        try {
            if (requestBody == null) {
                logger.warn("Received null request body");
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Request body is null"));
            }
            
            NavigationItem navigationItem = new NavigationItem();
            
            // 解析 ID
            String idStr = requestBody.get("id") != null ? requestBody.get("id").toString() : null;
            if (idStr != null && !idStr.equals("undefined") && !idStr.isEmpty()) {
                try {
                    long id = Long.parseLong(idStr);
                    if (id != 0) {
                        navigationItem.setId(id);
                    }
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "無效的ID格式"));
                }
            }
            
            // 解析標題
            String title = requestBody.get("title") != null ? requestBody.get("title").toString() : null;
            if (title == null || title.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "標題不能為空"));
            }
            navigationItem.setTitle(title);
            
            // 設置 URL
            navigationItem.setUrl((String) requestBody.get("url"));
            
            // 解析 open_in_new_tab
            Object openInNewTabObj = requestBody.get("openInNewTab");
            boolean openInNewTab = openInNewTabObj != null && 
                (openInNewTabObj.toString().equals("true") || 
                 openInNewTabObj.toString().equals("on"));
            navigationItem.setOpenInNewTab(openInNewTab);
            
            // 解析 sort_order
            String sortOrderStr = requestBody.get("sortOrder") != null ? 
                requestBody.get("sortOrder").toString() : "0";
            navigationItem.setSortOrder(Integer.valueOf(sortOrderStr));
            
            // 解析 is_active
            Object isActiveObj = requestBody.get("isActive");
            boolean isActive = isActiveObj != null && 
                (isActiveObj.toString().equals("true") || 
                 isActiveObj.toString().equals("on"));
            navigationItem.setIsActive(isActive);
            
            logger.debug("Processed navigation item: {}", navigationItem);
            
            // 保存並獲取結果
            NavigationItem savedItem = navigationService.saveNavigationItem(navigationItem);
            logger.debug("Saved navigation item: {}", savedItem);
            
            if (savedItem != null && savedItem.getId() != null) {
                return ResponseEntity.ok(Map.of("success", true, "message", "導航欄項目儲存成功"));
            } else {
                logger.warn("Failed to save navigation item: {}", navigationItem);
                return ResponseEntity.status(500)
                    .body(Map.of("success", false, "message", "儲存導航欄項目失敗"));
            }
        } catch (Exception e) {
            logger.error("Error saving navigation item", e);
            return ResponseEntity.status(500)
                .body(Map.of("success", false, "message", "儲存時發生錯誤: " + e.getMessage()));
        }
    }
    
    @GetMapping("/navigation/{id}")
    @ResponseBody
    public ResponseEntity<?> getNavigation(@PathVariable Long id) {
        try {
            logger.debug("Getting navigation item with id: {}", id);
            NavigationItem item = navigationService.getNavigationItemById(id);
            if (item == null) {
                logger.warn("Navigation item not found with id: {}", id);
                return ResponseEntity.status(404)
                    .body(Map.of("success", false, "message", "找不到導航欄項目"));
            }
            return ResponseEntity.ok(item.toMap());
        } catch (Exception e) {
            logger.error("Error getting navigation item with id: {}", id, e);
            return ResponseEntity.status(500)
                .body(Map.of("success", false, "message", "載入數據失敗: " + e.getMessage()));
        }
    }

    @DeleteMapping("/navigation/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteNavigation(@PathVariable Long id) {
        try {
            logger.debug("Deleting navigation item with id: {}", id);
            navigationService.deleteNavigationItem(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "導航欄項目刪除成功"));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid navigation item id: {}", id, e);
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "message", "無效的導航欄項目ID"));
        } catch (RuntimeException e) {
            logger.error("Error deleting navigation item with id: {}", id, e);
            return ResponseEntity.status(500)
                .body(Map.of("success", false, "message", "刪除時發生錯誤: " + e.getMessage()));
        }
    }
    
    // 網站內容管理
    @GetMapping("/content")
    public String contentManagement(Model model) {
        List<SiteContent> siteContents = siteContentService.getAllActiveContent();
        model.addAttribute("siteContents", siteContents);
        return "admin/content";
    }
    
    @PostMapping("/content")
    @ResponseBody
    public Map<String, Object> saveContent(@RequestBody SiteContent siteContent) {
        try {
            siteContentService.saveContent(siteContent);
            return Map.of("success", true, "message", "內容儲存成功");
        } catch (IllegalArgumentException e) {
            logger.error("Invalid content data", e);
            return Map.of("success", false, "message", "無效的內容數據: " + e.getMessage());
        } catch (RuntimeException e) {
            logger.error("Error saving content", e);
            return Map.of("success", false, "message", "儲存失敗: " + e.getMessage());
        }
    }

    @GetMapping("/content/{id}")
    @ResponseBody
    public ResponseEntity<?> getContent(@PathVariable Long id) {
        try {
            logger.debug("Getting content with id: {}", id);
            SiteContent content = siteContentService.getContentById(id);
            if (content != null) {
                return ResponseEntity.ok(content.toMap());
            }
            return ResponseEntity.status(404).body(Map.of("message", "找不到指定的內容"));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid content id: {}", id, e);
            return ResponseEntity.badRequest().body(Map.of("message", "無效的內容ID"));
        } catch (RuntimeException e) {
            logger.error("Error getting content with id: {}", id, e);
            return ResponseEntity.status(500).body(Map.of("message", "獲取內容時發生錯誤: " + e.getMessage()));
        }
    }

    @DeleteMapping("/content/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteContent(@PathVariable Long id) {
        try {
            siteContentService.deleteContent(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "內容刪除成功"));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid content id: {}", id, e);
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "message", "無效的內容ID"));
        } catch (RuntimeException e) {
            logger.error("Error deleting content with id: {}", id, e);
            return ResponseEntity.status(500)
                .body(Map.of("success", false, "message", "刪除失敗: " + e.getMessage()));
        }
    }
    
    // 圖片管理
    @GetMapping("/images")
    public String imageManagement(Model model) {
        List<ImageResource> images = imageService.getAllActiveImages();
        model.addAttribute("images", images);
        return "admin/images";
    }
    
    @PostMapping("/images/upload")
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file,
                             @RequestParam String category,
                             @RequestParam(required = false) String description) {
        try {
            imageService.saveImage(file, category, description);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
    
    @PostMapping("/images/update/{id}")
    @ResponseBody
    public String updateImage(@PathVariable Long id,
                             @RequestParam(value = "file", required = false) MultipartFile file,
                             @RequestParam(required = false) String category,
                             @RequestParam(required = false) String description) {
        try {
            imageService.updateImage(id, file, category, description);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
    
    @DeleteMapping("/images/{id}")
    @ResponseBody
    public String deleteImage(@PathVariable Long id) {
        try {
            imageService.deleteImage(id);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
    
    // 團隊成員管理
    @GetMapping("/team")
    public String teamManagement(Model model) {
        List<TeamMember> teamMembers = teamMemberService.getActiveMembers();
        model.addAttribute("teamMembers", teamMembers);
        return "admin/team";
    }
    
    @PostMapping("/team")
    @ResponseBody
    public String saveTeamMember(@ModelAttribute TeamMember teamMember) {
        teamMemberService.saveMember(teamMember);
        return "success";
    }
    
    // 隊聚活動照片管理
    @GetMapping("/events")
    public String eventManagement(Model model) {
        List<EventPhoto> eventPhotos = eventPhotoService.getActivePhotos();
        List<Integer> activeYears = eventPhotoService.getActiveYears();
        model.addAttribute("eventPhotos", eventPhotos);
        model.addAttribute("activeYears", activeYears);
        return "admin/events";
    }
    
    @PostMapping("/events")
    @ResponseBody
    public String saveEventPhoto(@ModelAttribute EventPhoto eventPhoto) {
        eventPhotoService.savePhoto(eventPhoto);
        return "success";
    }
    
    // 統計數據管理
    @GetMapping("/statistics")
    public String statisticsManagement(Model model) {
        List<Statistic> statistics = statisticService.getActiveStatistics();
        model.addAttribute("statistics", statistics);
        return "admin/statistics";
    }
    
    @PostMapping("/statistics")
    @ResponseBody
    public String saveStatistic(@ModelAttribute Statistic statistic) {
        statisticService.saveStatistic(statistic);
        return "success";
    }
}
