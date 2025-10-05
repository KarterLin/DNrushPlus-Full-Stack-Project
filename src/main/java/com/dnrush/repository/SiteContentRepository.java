package com.dnrush.repository;

import com.dnrush.entity.SiteContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SiteContentRepository extends JpaRepository<SiteContent, Long> {
    
    Optional<SiteContent> findByContentKeyAndIsActiveTrue(String contentKey);
    
    List<SiteContent> findBySectionAndIsActiveTrueOrderBySortOrder(String section);
    
    List<SiteContent> findByIsActiveTrueOrderBySectionAscSortOrderAsc();
    
    @Query("SELECT s FROM SiteContent s WHERE s.section = :section AND s.isActive = true ORDER BY s.sortOrder")
    List<SiteContent> findActiveBySection(String section);
    
    @Query("SELECT s FROM SiteContent s WHERE s.contentKey = :contentKey AND s.isActive = true")
    Optional<SiteContent> findActiveByContentKey(String contentKey);
}
