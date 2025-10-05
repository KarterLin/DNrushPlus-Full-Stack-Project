package com.dnrush.repository;

import com.dnrush.entity.NavigationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NavigationItemRepository extends JpaRepository<NavigationItem, Long> {
    
    List<NavigationItem> findByParentIsNullAndIsActiveTrueOrderBySortOrder();
    
    List<NavigationItem> findByParentIdAndIsActiveTrueOrderBySortOrder(Long parentId);
    
    @Query("SELECT n FROM NavigationItem n WHERE n.parent IS NULL AND n.isActive = true ORDER BY n.sortOrder")
    List<NavigationItem> findActiveRootItems();
    
    @Query("SELECT n FROM NavigationItem n WHERE n.parent.id = :parentId AND n.isActive = true ORDER BY n.sortOrder")
    List<NavigationItem> findActiveChildrenByParentId(Long parentId);
}
