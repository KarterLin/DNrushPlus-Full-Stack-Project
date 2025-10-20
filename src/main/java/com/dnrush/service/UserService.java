package com.dnrush.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.dnrush.entity.User;
import com.dnrush.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 處理 OAuth2 登入後的用戶資訊
     */
    public User processOAuth2User(OAuth2User oauth2User) {
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String googleId = oauth2User.getAttribute("sub");
        String picture = oauth2User.getAttribute("picture");
        
        Optional<User> existingUser = userRepository.findByGoogleId(googleId);
        
        if (existingUser.isPresent()) {
            // 更新現有用戶的最後登入時間
            User user = existingUser.get();
            user.setLastLogin(LocalDateTime.now());
            // 更新可能變更的資訊
            user.setName(name);
            user.setEmail(email);
            user.setProfilePictureUrl(picture);
            return userRepository.save(user);
        } else {
            // 創建新用戶
            User newUser = new User(email, name, googleId);
            newUser.setProfilePictureUrl(picture);
            newUser.setLastLogin(LocalDateTime.now());
            return userRepository.save(newUser);
        }
    }
    
    /**
     * 根據 email 查找用戶
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * 根據 Google ID 查找用戶
     */
    public Optional<User> findByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId);
    }
    
    /**
     * 檢查用戶是否為管理員
     */
    public boolean isAdmin(User user) {
        return user != null && User.Role.ADMIN.equals(user.getRole());
    }
    
    /**
     * 將用戶設為管理員（只有現有管理員可以操作）
     */
    public User setUserAsAdmin(String email, User currentUser) {
        if (!isAdmin(currentUser)) {
            throw new SecurityException("Only administrators can promote users");
        }
        
        Optional<User> userOpt = findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRole(User.Role.ADMIN);
            return userRepository.save(user);
        } else {
            throw new RuntimeException("User not found: " + email);
        }
    }
    
    /**
     * 獲取所有用戶
     */
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * 根據 ID 查找用戶
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * 修改用戶角色
     */
    public User updateUserRole(Long userId, User.Role newRole, User currentUser) {
        if (!isAdmin(currentUser)) {
            throw new SecurityException("Only administrators can change user roles");
        }
        
        Optional<User> userOpt = findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRole(newRole);
            return userRepository.save(user);
        } else {
            throw new RuntimeException("User not found: " + userId);
        }
    }
    

}