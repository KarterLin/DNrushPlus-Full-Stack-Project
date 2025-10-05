package com.dnrush.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dnrush.entity.NavigationItem;

@Repository
public interface NavigationItemRepository extends JpaRepository<NavigationItem, Long> {
    
    @Query("SELECT n FROM NavigationItem n WHERE n.isActive = true ORDER BY n.sortOrder")
    List<NavigationItem> findByParentIsNullAndIsActiveTrueOrderBySortOrder();
    
    @Query("SELECT n FROM NavigationItem n ORDER BY n.sortOrder")
    List<NavigationItem> findAllOrderBySortOrder();
}
