package com.dnrush.service;

import com.dnrush.entity.ContactSubmission;
import com.dnrush.repository.ContactSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContactService {
    
    @Autowired
    private ContactSubmissionRepository contactSubmissionRepository;
    
    public ContactSubmission saveSubmission(ContactSubmission submission) {
        return contactSubmissionRepository.save(submission);
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
}
