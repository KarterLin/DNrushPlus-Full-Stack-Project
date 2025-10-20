package com.dnrush.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dnrush.entity.User;
import com.dnrush.service.UserService;

@Controller
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 獲取當前登入用戶的資訊 (AJAX API)
     */
    @GetMapping("/api/user")
    @ResponseBody
    public Map<String, Object> getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
        Map<String, Object> response = new HashMap<>();
        
        if (principal != null) {
            String googleId = principal.getAttribute("sub");
            String email = principal.getAttribute("email");
            String name = principal.getAttribute("name");
            String picture = principal.getAttribute("picture");
            
            // 查找或創建用戶
            Optional<User> userOpt = userService.findByGoogleId(googleId);
            User user;
            
            if (userOpt.isPresent()) {
                user = userOpt.get();
            } else {
                // 如果用戶不存在，處理登入邏輯會在 SecurityConfig 中的 OAuth2UserService 處理
                user = userService.processOAuth2User(principal);
            }
            
            response.put("authenticated", true);
            response.put("name", name);
            response.put("email", email);
            response.put("profilePicture", picture);
            response.put("isAdmin", userService.isAdmin(user));
        } else {
            response.put("authenticated", false);
        }
        
        return response;
    }
    
    /**
     * 登入頁面 (如果需要自訂登入頁面)
     */
    @GetMapping("/login")
    public String loginPage() {
        return "redirect:/oauth2/authorization/google";
    }
}