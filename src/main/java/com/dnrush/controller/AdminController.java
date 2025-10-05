package com.dnrush.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
            // 獲取所有導航項目，包括未啟用的
            List<NavigationItem> navigationItems = navigationService.getAllItems();
            System.out.println("Retrieved navigation items: " + navigationItems);
            
            // 獲取所有啟用的導航項目（用於父項目下拉選單）
            List<NavigationItem> activeItems = navigationService.getAllActiveItems();
            
            model.addAttribute("navigationItems", navigationItems);
            model.addAttribute("activeItems", activeItems);
            return "admin/navigation";
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in navigationManagement: " + e.getMessage());
            model.addAttribute("error", "加載導航欄項目時出錯: " + e.getMessage());
            return "error";  // 這裡需要創建一個 error.html 模板
        }
    }
    
    @PostMapping("/navigation")
    @ResponseBody
    public String saveNavigation(@RequestBody Map<String, Object> requestBody) {
        try {
            NavigationItem navigationItem = new NavigationItem();
            
            // 解析 ID
            Object idObj = requestBody.get("id");
            if (idObj != null && !idObj.toString().equals("undefined") && !idObj.toString().isEmpty()) {
                try {
                    Long id = Long.parseLong(idObj.toString());
                    if (id != 0) {
                        navigationItem.setId(id);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid ID format: " + idObj);
                }
            }
            
            // 解析其他字段
            navigationItem.setTitle((String) requestBody.get("title"));
            navigationItem.setUrl((String) requestBody.get("url"));
            
            // 解析 open_in_new_tab
            Object openInNewTabObj = requestBody.get("openInNewTab");
            boolean openInNewTab = openInNewTabObj != null && 
                (openInNewTabObj.toString().equals("true") || 
                 openInNewTabObj.toString().equals("on"));
            navigationItem.setOpenInNewTab(openInNewTab);
            
            // 解析 parent_id
            Object parentObj = requestBody.get("parent");
            if (parentObj instanceof Map) {
                Map<String, Object> parentMap = (Map<String, Object>) parentObj;
                Object parentId = parentMap.get("id");
                if (parentId != null && !parentId.toString().equals("undefined") && !parentId.toString().isEmpty()) {
                    try {
                        Long pid = Long.parseLong(parentId.toString());
                        if (pid != 0) {
                            NavigationItem parent = navigationService.getNavigationItemById(pid);
                            navigationItem.setParent(parent);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid parent ID format: " + parentId);
                    }
                }
            }
            
            // 解析 sort_order
            Object sortOrderObj = requestBody.get("sortOrder");
            if (sortOrderObj != null) {
                try {
                    navigationItem.setSortOrder(Integer.parseInt(sortOrderObj.toString()));
                } catch (NumberFormatException e) {
                    navigationItem.setSortOrder(0);
                }
            } else {
                navigationItem.setSortOrder(0);
            }
            
            // 解析 is_active
            Object isActiveObj = requestBody.get("isActive");
            boolean isActive = isActiveObj != null && 
                (isActiveObj.toString().equals("true") || 
                 isActiveObj.toString().equals("on"));
            navigationItem.setIsActive(isActive);
            
            System.out.println("Processed navigation item: " + navigationItem);
            
            // 保存並獲取結果
            NavigationItem savedItem = navigationService.saveNavigationItem(navigationItem);
            System.out.println("Saved navigation item: " + savedItem);
            
            if (savedItem != null && savedItem.getId() != null) {
                return "success";
            } else {
                return "error: Failed to save navigation item";
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error saving navigation item: " + e.getMessage());
            return "error: " + e.getMessage();
        }
    }
    
    @GetMapping("/navigation/{id}")
    @ResponseBody
    public NavigationItem getNavigation(@PathVariable Long id) {
        try {
            System.out.println("Getting navigation item with id: " + id);
            return navigationService.getNavigationItemById(id);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error getting navigation item: " + e.getMessage());
            throw new RuntimeException("無法獲取導航欄項目", e);
        }
    }

    @DeleteMapping("/navigation/{id}")
    @ResponseBody
    public String deleteNavigation(@PathVariable Long id) {
        try {
            System.out.println("Deleting navigation item with id: " + id);
            navigationService.deleteNavigationItem(id);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error deleting navigation item: " + e.getMessage());
            return "error: " + e.getMessage();
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
    public String saveContent(@ModelAttribute SiteContent siteContent) {
        siteContentService.saveContent(siteContent);
        return "success";
    }
    
    @PostMapping("/content/update")
    @ResponseBody
    public String updateContent(@RequestParam String contentKey, @RequestParam String content) {
        siteContentService.updateContent(contentKey, content);
        return "success";
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
