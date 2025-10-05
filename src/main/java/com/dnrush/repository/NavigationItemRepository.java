package com.dnrush.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dnrush.entity.NavigationItem;

@Repository
public interface NavigationItemRepository extends JpaRepository<NavigationItem, Long> {
    
    List<NavigationItem> findByParentIsNullAndIsActiveTrueOrderBySortOrder();
    
    List<NavigationItem> findByParentIdAndIsActiveTrueOrderBySortOrder(Long parentId);
    
    @Query("SELECT n FROM NavigationItem n WHERE n.parent IS NULL AND n.isActive = true ORDER BY n.sortOrder")
    List<NavigationItem> findActiveRootItems();
    
    @Query("SELECT n FROM NavigationItem n WHERE n.parent.id = :parentId AND n.isActive = true ORDER BY n.sortOrder")
    List<NavigationItem> findActiveChildrenByParentId(Long parentId);
    
    @Query("SELECT n FROM NavigationItem n WHERE n.parent IS NULL ORDER BY n.sortOrder")
    List<NavigationItem> findAllRootItems();
}
