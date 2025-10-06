package com.dnrush.service;

import com.dnrush.entity.ContactSubmission;
import com.dnrush.repository.ContactSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ContactService {
    
    @Autowired
    private ContactSubmissionRepository contactSubmissionRepository;
    
    // 用來儲存正在處理的請求 ID，防止重複提交
    private final ConcurrentHashMap<String, Long> processingRequests = new ConcurrentHashMap<>();
    
    public ContactSubmission saveSubmission(ContactSubmission submission) {
        // 檢查是否在過去 30 秒內有相同的提交
        LocalDateTime thirtySecondsAgo = LocalDateTime.now().minusSeconds(30);
        List<ContactSubmission> recentSubmissions = contactSubmissionRepository
            .findRecentSubmissionsByEmailAndMessage(submission.getEmail(), submission.getMessage(), thirtySecondsAgo);
        
        if (!recentSubmissions.isEmpty()) {
            throw new RuntimeException("重複提交：請勿在短時間內重複提交相同內容");
        }
        
        return contactSubmissionRepository.save(submission);
    }
    
    public ContactSubmission saveSubmissionWithRequestId(ContactSubmission submission, String requestId) {
        // 如果有 requestId，檢查是否正在處理中
        if (requestId != null && !requestId.trim().isEmpty()) {
            Long currentTime = System.currentTimeMillis();
            
            // 檢查是否已經在處理這個請求
            if (processingRequests.containsKey(requestId)) {
                throw new RuntimeException("重複請求：此請求正在處理中 (ID: " + requestId + ")");
            }
            
            // 標記為正在處理
            processingRequests.put(requestId, currentTime);
            
            try {
                // 執行正常的重複檢查和保存
                ContactSubmission saved = saveSubmission(submission);
                
                // 成功後，延遲移除以確保短時間內不會重複
                removeRequestIdAfterDelay(requestId, 10000); // 10秒後移除
                
                return saved;
            } catch (Exception e) {
                // 發生錯誤時立即移除
                processingRequests.remove(requestId);
                throw e;
            }
        } else {
            // 沒有 requestId 的情況，使用原來的邏輯
            return saveSubmission(submission);
        }
    }
    
    private void removeRequestIdAfterDelay(String requestId, long delayMs) {
        new Thread(() -> {
            try {
                Thread.sleep(delayMs);
                processingRequests.remove(requestId);
                System.out.println("已移除請求 ID: " + requestId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    public List<ContactSubmission> getUnprocessedSubmissions() {
        return contactSubmissionRepository.findUnprocessedSubmissions();
    }
    
    public List<ContactSubmission> getAllSubmissions() {
        return contactSubmissionRepository.findAllByOrderByCreatedAtDesc();
    }
    
    public ContactSubmission getSubmissionById(Long id) {
        return contactSubmissionRepository.findById(id).orElse(null);
    }
    
    public ContactSubmission markAsProcessed(Long id) {
        ContactSubmission submission = getSubmissionById(id);
        if (submission != null) {
            submission.setIsProcessed(true);
            submission.setProcessedAt(LocalDateTime.now());
            return contactSubmissionRepository.save(submission);
        }
        return null;
    }
    
    public void deleteSubmission(Long id) {
        contactSubmissionRepository.deleteById(id);
    }
    
    public List<ContactSubmission> getSubmissionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return contactSubmissionRepository.findByDateRange(startDate, endDate);
    }
    
    /**
     * 獲取聯絡表單總數量
     */
    public long getTotalCount() {
        return contactSubmissionRepository.count();
    }
    
    /**
     * 獲取最後更新時間
     */
    public String getLastUpdateTime() {
        List<ContactSubmission> submissions = contactSubmissionRepository.findAllByOrderByCreatedAtDesc();
        if (!submissions.isEmpty()) {
            LocalDateTime lastUpdate = submissions.get(0).getCreatedAt();
            return lastUpdate.toString().replace("T", " ");
        }
        return "無記錄";
    }
}
