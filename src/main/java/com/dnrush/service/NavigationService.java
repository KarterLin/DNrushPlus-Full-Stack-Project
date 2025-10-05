package com.dnrush.service;

import com.dnrush.entity.NavigationItem;
import com.dnrush.repository.NavigationItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NavigationService {
    
    @Autowired
    private NavigationItemRepository navigationItemRepository;
    
    public List<NavigationItem> getActiveRootItems() {
        return navigationItemRepository.findActiveRootItems();
    }
    
    public List<NavigationItem> getActiveChildrenByParentId(Long parentId) {
        return navigationItemRepository.findActiveChildrenByParentId(parentId);
    }
    
    public List<NavigationItem> getAllActiveItems() {
        return navigationItemRepository.findByParentIsNullAndIsActiveTrueOrderBySortOrder();
    }
    
    public NavigationItem saveNavigationItem(NavigationItem navigationItem) {
        return navigationItemRepository.save(navigationItem);
    }
    
    public void deleteNavigationItem(Long id) {
        navigationItemRepository.deleteById(id);
    }
    
    public NavigationItem getNavigationItemById(Long id) {
        return navigationItemRepository.findById(id).orElse(null);
    }
}
