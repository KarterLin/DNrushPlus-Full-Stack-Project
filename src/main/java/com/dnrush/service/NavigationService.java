package com.dnrush.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dnrush.entity.NavigationItem;
import com.dnrush.repository.NavigationItemRepository;

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
    
    public List<NavigationItem> getAllItems() {
        try {
            System.out.println("Getting all navigation items");
            List<NavigationItem> items = navigationItemRepository.findAllRootItems();
            System.out.println("Found " + items.size() + " navigation items");
            return items;
        } catch (Exception e) {
            System.err.println("Error getting navigation items: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("無法獲取導航欄項目", e);
        }
    }
    
    public List<NavigationItem> getAllActiveItems() {
        try {
            System.out.println("Getting all active navigation items");
            List<NavigationItem> items = navigationItemRepository.findByParentIsNullAndIsActiveTrueOrderBySortOrder();
            System.out.println("Found " + items.size() + " active navigation items");
            return items;
        } catch (Exception e) {
            System.err.println("Error getting active navigation items: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("無法獲取導航欄項目", e);
        }
    }
    
    public NavigationItem saveNavigationItem(NavigationItem navigationItem) {
        try {
            System.out.println("Saving navigation item: " + navigationItem);
            NavigationItem saved = navigationItemRepository.save(navigationItem);
            System.out.println("Successfully saved navigation item: " + saved);
            return saved;
        } catch (Exception e) {
            System.err.println("Error saving navigation item: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("無法保存導航欄項目", e);
        }
    }
    
    public void deleteNavigationItem(Long id) {
        try {
            System.out.println("Deleting navigation item with id: " + id);
            navigationItemRepository.deleteById(id);
            System.out.println("Successfully deleted navigation item with id: " + id);
        } catch (Exception e) {
            System.err.println("Error deleting navigation item: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("無法刪除導航欄項目", e);
        }
    }
    
    public NavigationItem getNavigationItemById(Long id) {
        try {
            System.out.println("Getting navigation item by id: " + id);
            NavigationItem item = navigationItemRepository.findById(id).orElse(null);
            System.out.println("Found navigation item: " + item);
            return item;
        } catch (Exception e) {
            System.err.println("Error getting navigation item by id: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("無法獲取導航欄項目", e);
        }
    }
}
