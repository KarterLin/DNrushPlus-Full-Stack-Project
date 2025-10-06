package com.dnrush.service;

import com.dnrush.entity.ImageResource;
import com.dnrush.entity.TeamMember;
import com.dnrush.repository.TeamMemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class TeamMemberService {
    
    private static final Logger logger = LoggerFactory.getLogger(TeamMemberService.class);
    
    @Autowired
    private TeamMemberRepository teamMemberRepository;
    
    @Autowired
    private ImageService imageService;
    
    public List<TeamMember> getActiveMembers() {
        return teamMemberRepository.findActiveMembers();
    }
    
    public TeamMember saveMember(TeamMember teamMember) {
        return teamMemberRepository.save(teamMember);
    }
    
    public TeamMember saveTeamMemberWithAvatar(TeamMember teamMember, MultipartFile avatarFile) throws Exception {
        try {
            // 處理頭像上傳
            if (avatarFile != null && !avatarFile.isEmpty()) {
                // 生成唯一檔名
                String fileName = UUID.randomUUID().toString() + "_" + avatarFile.getOriginalFilename();
                
                // 轉換為 Base64
                byte[] fileBytes = avatarFile.getBytes();
                String base64Data = "data:" + avatarFile.getContentType() + ";base64," + Base64.getEncoder().encodeToString(fileBytes);
                
                // 創建 ImageResource
                ImageResource avatarImage = new ImageResource();
                avatarImage.setName(fileName);
                avatarImage.setOriginalName(avatarFile.getOriginalFilename());
                avatarImage.setMimeType(avatarFile.getContentType());
                avatarImage.setFileSize(avatarFile.getSize());
                avatarImage.setCategory("avatar");
                avatarImage.setBase64Data(base64Data);
                avatarImage.setDescription("Team member avatar: " + teamMember.getName());
                avatarImage.setIsActive(true);
                
                // 保存圖片
                ImageResource savedImage = imageService.saveImageResource(avatarImage);
                
                // 設置團隊成員的頭像
                teamMember.setAvatarImage(savedImage);
            }
            
            // 保存團隊成員
            return teamMemberRepository.save(teamMember);
            
        } catch (Exception e) {
            logger.error("保存團隊成員頭像失敗: {}", e.getMessage(), e);
            throw new Exception("保存團隊成員頭像失敗: " + e.getMessage());
        }
    }
    
    public void deleteMember(Long id) {
        try {
            TeamMember member = getMemberById(id);
            if (member != null) {
                // 如果有頭像，也要刪除對應的圖片
                if (member.getAvatarImage() != null) {
                    imageService.deleteImage(member.getAvatarImage().getId());
                }
                
                // 軟刪除：設置為非活躍狀態
                member.setIsActive(false);
                teamMemberRepository.save(member);
            }
        } catch (Exception e) {
            logger.error("刪除團隊成員失敗: {}", e.getMessage(), e);
            throw new RuntimeException("刪除團隊成員失敗: " + e.getMessage());
        }
    }
    
    public TeamMember getMemberById(Long id) {
        return teamMemberRepository.findByIdWithAvatar(id).orElse(null);
    }
    
    public List<TeamMember> getAllMembers() {
        return teamMemberRepository.findAll();
    }
    
    /**
     * 獲取活躍團隊成員數量
     */
    public long getActiveCount() {
        return teamMemberRepository.findActiveMembers().size();
    }
}
