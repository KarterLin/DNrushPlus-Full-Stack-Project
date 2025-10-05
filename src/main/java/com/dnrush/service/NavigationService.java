package com.dnrush.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnrush.entity.NavigationItem;
import com.dnrush.repository.NavigationItemRepository;

@Service
public class NavigationService {

    private static final Logger logger = LoggerFactory.getLogger(NavigationService.class);
    
    @Autowired
    private NavigationItemRepository navigationItemRepository;
    

    

    
    @Transactional(readOnly = true)
    public List<NavigationItem> getActiveRootItems() {
        try {
            List<NavigationItem> items = navigationItemRepository.findByParentIsNullAndIsActiveTrueOrderBySortOrder();
            return items;
        } catch (Exception e) {
            logger.error("Error getting active root items", e);
            throw new RuntimeException("無法獲取導航欄項目", e);
        }
    }
    

    
    @Transactional(readOnly = true)
    public List<NavigationItem> getAllItems() {
        try {
            logger.debug("Getting all navigation items");
            List<NavigationItem> items = navigationItemRepository.findAllOrderBySortOrder();
            logger.debug("Found {} navigation items", items.size());
            return items;
        } catch (Exception e) {
            logger.error("Error getting navigation items", e);
            throw new RuntimeException("無法獲取導航欄項目", e);
        }
    }
    
    public List<NavigationItem> getAllActiveItems() {
        try {
            System.out.println("Getting all active navigation items");
            List<NavigationItem> items = navigationItemRepository.findByParentIsNullAndIsActiveTrueOrderBySortOrder();
            System.out.println("Found " + items.size() + " active navigation items");
            return items;
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid argument while getting active navigation items: " + e.getMessage());
            throw new RuntimeException("無法獲取導航欄項目：參數無效", e);
        } catch (RuntimeException e) {
            System.err.println("Error getting active navigation items: " + e.getMessage());
            throw new RuntimeException("無法獲取導航欄項目：資料庫錯誤", e);
        }
    }
    
    @Transactional
    public NavigationItem saveNavigationItem(NavigationItem navigationItem) {
        try {
            logger.debug("Saving navigation item: {}", navigationItem);
            NavigationItem saved = navigationItemRepository.save(navigationItem);
            logger.debug("Successfully saved navigation item: {}", saved);
            return saved;
        } catch (Exception e) {
            logger.error("Error saving navigation item: {}", navigationItem, e);
            throw new RuntimeException("無法保存導航欄項目", e);
        }
    }
    
    @Transactional
    public void deleteNavigationItem(Long id) {
        try {
            logger.debug("Deleting navigation item with id: {}", id);
            navigationItemRepository.deleteById(id);
            logger.debug("Successfully deleted navigation item with id: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting navigation item with id: {}", id, e);
            throw new RuntimeException("無法刪除導航欄項目", e);
        }
    }
    
    @Transactional(readOnly = true)
    public NavigationItem getNavigationItemById(Long id) {
        try {
            logger.debug("Getting navigation item by id: {}", id);
            NavigationItem item = navigationItemRepository.findById(id).orElse(null);
            logger.debug("Found navigation item: {}", item);
            return item;
        } catch (Exception e) {
            logger.error("Error getting navigation item by id: {}", id, e);
            throw new RuntimeException("無法獲取導航欄項目", e);
        }
    }
    

}
