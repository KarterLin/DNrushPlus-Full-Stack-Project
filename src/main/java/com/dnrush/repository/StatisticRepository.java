package com.dnrush.repository;

import com.dnrush.entity.Statistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatisticRepository extends JpaRepository<Statistic, Long> {
    
    List<Statistic> findByIsActiveTrueOrderBySortOrder();
    
    Optional<Statistic> findByStatKeyAndIsActiveTrue(String statKey);
    
    @Query("SELECT s FROM Statistic s WHERE s.isActive = true ORDER BY s.sortOrder")
    List<Statistic> findActiveOrdered();
}
