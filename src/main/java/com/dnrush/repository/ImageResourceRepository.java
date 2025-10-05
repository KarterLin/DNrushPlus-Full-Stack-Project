package com.dnrush.repository;

import com.dnrush.entity.ImageResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageResourceRepository extends JpaRepository<ImageResource, Long> {
    
    List<ImageResource> findByCategoryAndIsActiveTrueOrderByCreatedAtDesc(String category);
    
    List<ImageResource> findByIsActiveTrueOrderByCreatedAtDesc();
    
    Optional<ImageResource> findByNameAndIsActiveTrue(String name);
    
    @Query("SELECT i FROM ImageResource i WHERE i.category = :category AND i.isActive = true ORDER BY i.createdAt DESC")
    List<ImageResource> findActiveByCategory(String category);
    
    @Query("SELECT i FROM ImageResource i WHERE i.base64Data IS NOT NULL AND i.isActive = true")
    List<ImageResource> findActiveWithBase64Data();
}
