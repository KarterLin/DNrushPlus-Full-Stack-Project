package com.dnrush.controller;

import java.util.HashMap;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.dnrush.entity.ImageResource;
import com.dnrush.entity.NavigationItem;
import com.dnrush.entity.SiteContent;
import com.dnrush.entity.TeamMember;
import com.dnrush.entity.ContactSubmission;
import com.dnrush.service.ImageService;
import com.dnrush.service.NavigationService;
import com.dnrush.service.SiteContentService;
import com.dnrush.service.TeamMemberService;
import com.dnrush.service.ContactService;

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
    private ContactService contactService;
    
    // 管理首頁
    @GetMapping
    public String adminIndex(Model model) {
        try {
            // 統計數據
            long contactSubmissionCount = contactService.getTotalCount();
            long activeTeamMemberCount = teamMemberService.getActiveCount();
            long activeImageCount = imageService.getActiveCount();
            
            // 獲取最後更新時間（資料庫最後異動時間）
            String lastUpdateTime = getLastDatabaseUpdateTime();
            
            model.addAttribute("contactSubmissionCount", contactSubmissionCount);
            model.addAttribute("activeTeamMemberCount", activeTeamMemberCount);
            model.addAttribute("activeImageCount", activeImageCount);
            model.addAttribute("lastUpdateTime", lastUpdateTime);
            
            return "admin/index";
        } catch (Exception e) {
            logger.error("載入儀表板數據時出錯", e);
            // 設置預設值以防錯誤
            model.addAttribute("contactSubmissionCount", 0L);
            model.addAttribute("activeTeamMemberCount", 0L);
            model.addAttribute("activeImageCount", 0L);
            model.addAttribute("lastUpdateTime", "無法獲取");
            return "admin/index";
        }
    }
    
    /**
     * 獲取資料庫最後更新時間
     * 查詢所有主要表的最新記錄更新時間
     */
    private String getLastDatabaseUpdateTime() {
        try {
            // 這裡可以查詢各表的最新更新時間，選擇最新的一個
            // 暫時使用聯絡表單的最新時間作為示例
            return contactService.getLastUpdateTime();
        } catch (Exception e) {
            logger.warn("無法獲取資料庫最後更新時間", e);
            return "無法獲取";
        }
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
    public String imageManagement(@RequestParam(required = false) String category, 
                                 Model model) {
        // 獲取圖片列表
        List<ImageResource> images;
        if (category != null && !category.isEmpty()) {
            images = imageService.getImagesByCategory(category);
            model.addAttribute("selectedCategory", category);
        } else {
            images = imageService.getAllActiveImages();
            model.addAttribute("selectedCategory", "all");
        }
        
        // 簡化邏輯 - 直接載入所有圖片，不需要分頁
        model.addAttribute("images", images);
        
        // 添加年份列表供選擇
        List<Integer> availableYears = imageService.getDistinctYears();
        model.addAttribute("availableYears", availableYears);
        
        return "admin/images";
    }
    
    @PostMapping("/images/upload")
    @ResponseBody
    public String uploadImage(@RequestParam(value = "file", required = false) MultipartFile file,
                             @RequestParam String category,
                             @RequestParam(required = false) String description,
                             @RequestParam(required = false) String year,
                             @RequestParam(required = false) String id) {
        try {
            Integer yearValue = null;
            if (year != null && !year.isEmpty()) {
                yearValue = Integer.parseInt(year);
            }
            
            // 如果有 id 參數，表示是更新操作
            if (id != null && !id.isEmpty()) {
                Long imageId = Long.parseLong(id);
                imageService.updateImage(imageId, file, category, description, yearValue);
            } else {
                // 新增操作 - 檔案必須存在
                if (file == null || file.isEmpty()) {
                    return "error: 請選擇要上傳的圖片檔案";
                }
                imageService.saveImage(file, category, description, yearValue);
            }
            return "success";
        } catch (Exception e) {
            logger.error("Error processing image", e);
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
    
    @PostMapping("/images/years/check")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkYearCanDelete(@RequestParam Integer year) {
        try {
            boolean canDelete = imageService.canDeleteYear(year);
            Map<String, Object> response = new HashMap<>();
            response.put("canDelete", canDelete);
            response.put("year", year);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @DeleteMapping("/images/years/{year}")
    @ResponseBody
    public String deleteYear(@PathVariable Integer year) {
        try {
            if (!imageService.canDeleteYear(year)) {
                return "error: 該年份仍有照片存在，無法刪除";
            }
            imageService.deleteYear(year);
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
    public String saveTeamMember(@RequestParam("name") String name,
                                @RequestParam("position") String position,
                                @RequestParam(value = "facebookUrl", required = false) String facebookUrl,
                                @RequestParam(value = "instagramUrl", required = false) String instagramUrl,
                                @RequestParam(value = "youtubeUrl", required = false) String youtubeUrl,
                                @RequestParam(value = "githubUrl", required = false) String githubUrl,
                                @RequestParam(value = "description", required = false) String description,
                                @RequestParam(value = "sortOrder", required = false, defaultValue = "0") Integer sortOrder,
                                @RequestParam(value = "avatar", required = false) MultipartFile avatarFile,
                                @RequestParam(value = "id", required = false) Long id) {
        try {
            TeamMember teamMember;
            if (id != null && id > 0) {
                teamMember = teamMemberService.getMemberById(id);
                if (teamMember == null) {
                    return "error: 找不到指定的團隊成員";
                }
            } else {
                teamMember = new TeamMember();
            }
            
            teamMember.setName(name);
            teamMember.setPosition(position);
            teamMember.setFacebookUrl(facebookUrl);
            teamMember.setInstagramUrl(instagramUrl);
            teamMember.setYoutubeUrl(youtubeUrl);
            teamMember.setGithubUrl(githubUrl);
            teamMember.setDescription(description);
            teamMember.setSortOrder(sortOrder);
            
            // 處理頭像上傳
            if (avatarFile != null && !avatarFile.isEmpty()) {
                teamMemberService.saveTeamMemberWithAvatar(teamMember, avatarFile);
            } else {
                teamMemberService.saveMember(teamMember);
            }
            
            return "success";
        } catch (Exception e) {
            logger.error("保存團隊成員失敗", e);
            return "error: " + e.getMessage();
        }
    }
    
    @DeleteMapping("/team/{id}")
    @ResponseBody
    public String deleteTeamMember(@PathVariable Long id) {
        try {
            teamMemberService.deleteMember(id);
            return "success";
        } catch (Exception e) {
            logger.error("刪除團隊成員失敗", e);
            return "error: " + e.getMessage();
        }
    }
    
    // 聯絡表單管理
    @GetMapping("/contact-submissions")
    public String contactSubmissions(Model model) {
        try {
            List<ContactSubmission> submissions = contactService.getAllSubmissions();
            model.addAttribute("submissions", submissions);
            return "admin/contact-submissions";
        } catch (Exception e) {
            logger.error("Error loading contact submissions", e);
            return "redirect:/admin?error=load-failed";
        }
    }
    
    @PostMapping("/contact/mark-processed/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> markAsProcessed(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            ContactSubmission submission = contactService.markAsProcessed(id);
            if (submission != null) {
                response.put("success", true);
                response.put("message", "已標記為已處理");
            } else {
                response.put("success", false);
                response.put("message", "找不到該提交記錄");
            }
        } catch (Exception e) {
            logger.error("Error marking submission as processed", e);
            response.put("success", false);
            response.put("message", "處理失敗");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/contact/delete/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteContactSubmission(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            contactService.deleteSubmission(id);
            response.put("success", true);
            response.put("message", "已刪除提交記錄");
        } catch (Exception e) {
            logger.error("Error deleting submission", e);
            response.put("success", false);
            response.put("message", "刪除失敗");
        }
        
        return ResponseEntity.ok(response);
    }
}
