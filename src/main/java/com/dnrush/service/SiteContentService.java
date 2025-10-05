package com.dnrush.service;

import com.dnrush.entity.SiteContent;
import com.dnrush.repository.SiteContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SiteContentService {
    
    @Autowired
    private SiteContentRepository siteContentRepository;
    
    public List<SiteContent> getContentBySection(String section) {
        return siteContentRepository.findActiveBySection(section);
    }
    
    public Optional<SiteContent> getContentByKey(String contentKey) {
        return siteContentRepository.findActiveByContentKey(contentKey);
    }
    
    public String getContentValue(String contentKey) {
        return siteContentRepository.findActiveByContentKey(contentKey)
                .map(SiteContent::getContent)
                .orElse("");
    }
    
    public String getContentValue(String contentKey, String defaultValue) {
        return siteContentRepository.findActiveByContentKey(contentKey)
                .map(SiteContent::getContent)
                .orElse(defaultValue);
    }
    
    public List<SiteContent> getAllActiveContent() {
        return siteContentRepository.findByIsActiveTrueOrderBySectionAscSortOrderAsc();
    }
    
    public SiteContent saveContent(SiteContent siteContent) {
        return siteContentRepository.save(siteContent);
    }
    
    public SiteContent updateContent(String contentKey, String content) {
        Optional<SiteContent> existingContent = siteContentRepository.findActiveByContentKey(contentKey);
        if (existingContent.isPresent()) {
            SiteContent siteContent = existingContent.get();
            siteContent.setContent(content);
            return siteContentRepository.save(siteContent);
        }
        return null;
    }
    
    public void deleteContent(Long id) {
        siteContentRepository.deleteById(id);
    }
    
    public SiteContent getContentById(Long id) {
        return siteContentRepository.findById(id).orElse(null);
    }
}
