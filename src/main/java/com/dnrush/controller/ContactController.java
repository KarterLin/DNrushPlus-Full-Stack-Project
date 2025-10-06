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
    public String submitContactForm(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam(name = "available_time", required = false) String availableTime,
            @RequestParam(required = false) String subject,
            @RequestParam String message,
            @RequestParam(required = false) String requestId,
            HttpServletRequest request) {
        
        try {
            // 檢查是否有 requestId，沒有的話直接拒絕
            if (requestId == null || requestId.trim().isEmpty()) {
                System.err.println("拒絕沒有 requestId 的請求");
                return "提交失敗：缺少請求標識符";
            }
            
            // 添加日誌來追蹤請求
            System.out.println("=== 聯絡表單提交請求 ===");
            System.out.println("時間: " + java.time.LocalDateTime.now());
            System.out.println("請求ID: " + requestId);
            System.out.println("姓名: " + name);
            System.out.println("電子郵件: " + email);
            System.out.println("可聯繫時間: " + availableTime);
            System.out.println("主題: " + subject);
            System.out.println("訊息: " + message.substring(0, Math.min(50, message.length())) + "...");
            System.out.println("=========================");
            
            // 創建聯絡表單提交記錄
            ContactSubmission submission = new ContactSubmission();
            submission.setName(name);
            submission.setEmail(email);
            submission.setAvailableTime(availableTime);
            submission.setSubject(subject);
            submission.setMessage(message);
            
            // 保存到資料庫
            ContactSubmission saved = contactService.saveSubmissionWithRequestId(submission, requestId);
            System.out.println("成功保存提交記錄，ID: " + saved.getId());
            
            // 返回 "OK" 給前端 validate.js
            return "OK";
            
        } catch (Exception e) {
            System.err.println("提交失敗: " + e.getMessage());
            // 返回錯誤信息
            return "提交失敗：" + e.getMessage();
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
}
