package com.dnrush.controller;

import com.dnrush.entity.*;
import com.dnrush.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
        List<NavigationItem> navigationItems = navigationService.getAllActiveItems();
        model.addAttribute("navigationItems", navigationItems);
        return "admin/navigation";
    }
    
    @PostMapping("/navigation")
    @ResponseBody
    public String saveNavigation(@ModelAttribute NavigationItem navigationItem) {
        navigationService.saveNavigationItem(navigationItem);
        return "success";
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
