package com.dnrush.repository;

import com.dnrush.entity.EventPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventPhotoRepository extends JpaRepository<EventPhoto, Long> {
    
    List<EventPhoto> findByIsActiveTrueOrderByEventYearDescEventMonthDescSortOrder();
    
    List<EventPhoto> findByEventYearAndIsActiveTrueOrderByEventMonthDescSortOrder(Integer eventYear);
    
    @Query("SELECT e FROM EventPhoto e WHERE e.isActive = true ORDER BY e.eventYear DESC, e.eventMonth DESC, e.sortOrder")
    List<EventPhoto> findActiveOrderedByDate();
    
    @Query("SELECT DISTINCT e.eventYear FROM EventPhoto e WHERE e.isActive = true ORDER BY e.eventYear DESC")
    List<Integer> findDistinctActiveYears();
}
