package com.dnrush.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dnrush.entity.ContactSubmission;
import com.dnrush.entity.NavigationItem;
import com.dnrush.service.ContactService;
import com.dnrush.service.NavigationService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/contact")
public class ContactController {
    
    @Autowired
    private ContactService contactService;
    
    @Autowired
    private NavigationService navigationService;
    
    @PostMapping("/submit")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> submitContactForm(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam(required = false) String availableTime,
            @RequestParam(required = false) String subject,
            @RequestParam String message,
            HttpServletRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 創建聯絡表單提交記錄
            ContactSubmission submission = new ContactSubmission();
            submission.setName(name);
            submission.setEmail(email);
            submission.setAvailableTime(availableTime);
            submission.setSubject(subject);
            submission.setMessage(message);
            submission.setIpAddress(getClientIpAddress(request));
            submission.setUserAgent(request.getHeader("User-Agent"));
            
            // 保存到資料庫
            ContactSubmission savedSubmission = contactService.saveSubmission(submission);
            
            response.put("success", true);
            response.put("message", "已成功提交，感謝您聯絡我們DN車隊！");
            response.put("submissionId", savedSubmission.getId());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "提交失敗，請稍後再試");
            response.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/submissions")
    public String getSubmissions(Model model) {
        // 導航欄資料
        List<NavigationItem> navigationItems = navigationService.getActiveRootItems();
        model.addAttribute("navigationItems", navigationItems);
        
        // 獲取所有提交記錄
        List<ContactSubmission> submissions = contactService.getAllSubmissions();
        model.addAttribute("submissions", submissions);
        
        return "admin/contact-submissions";
    }
    
    @PostMapping("/mark-processed/{id}")
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
            response.put("success", false);
            response.put("message", "處理失敗");
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteSubmission(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            contactService.deleteSubmission(id);
            response.put("success", true);
            response.put("message", "已刪除提交記錄");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "刪除失敗");
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
