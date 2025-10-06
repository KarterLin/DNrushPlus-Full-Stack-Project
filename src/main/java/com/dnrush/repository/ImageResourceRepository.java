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
    
    @Query("SELECT DISTINCT i.year FROM ImageResource i WHERE i.year IS NOT NULL AND i.isActive = true ORDER BY i.year DESC")
    List<Integer> findDistinctYears();
    
    @Query("SELECT i FROM ImageResource i WHERE i.category = :category AND i.year = :year AND i.isActive = true ORDER BY i.createdAt DESC")
    List<ImageResource> findActiveByCategoryAndYear(String category, Integer year);
    
    // 分頁查詢方法
    @Query(value = "SELECT * FROM image_resources WHERE is_active = :status ORDER BY year DESC, created_at DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<ImageResource> findByStatusOrderByYearDescCreatedAtDescWithPagination(boolean status, int offset, int limit);
    
    @Query(value = "SELECT * FROM image_resources WHERE category = :category AND is_active = :status ORDER BY year DESC, created_at DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<ImageResource> findByCategoryAndStatusOrderByYearDescCreatedAtDescWithPagination(String category, boolean status, int offset, int limit);
    
    @Query(value = "SELECT * FROM image_resources WHERE year = :year AND is_active = :status ORDER BY created_at DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<ImageResource> findByYearAndStatusOrderByCreatedAtDescWithPagination(String year, boolean status, int offset, int limit);
    
    @Query(value = "SELECT * FROM image_resources WHERE category = :category AND year = :year AND is_active = :status ORDER BY created_at DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<ImageResource> findByCategoryAndYearAndStatusOrderByCreatedAtDescWithPagination(String category, String year, boolean status, int offset, int limit);
}
