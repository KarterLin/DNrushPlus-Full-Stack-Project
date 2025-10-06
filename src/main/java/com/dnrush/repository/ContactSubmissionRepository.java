package com.dnrush.repository;

import com.dnrush.entity.ContactSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContactSubmissionRepository extends JpaRepository<ContactSubmission, Long> {
    
    List<ContactSubmission> findByIsProcessedFalseOrderByCreatedAtDesc();
    
    List<ContactSubmission> findByIsProcessedTrueOrderByProcessedAtDesc();
    
    List<ContactSubmission> findAllByOrderByCreatedAtDesc();
    
    @Query("SELECT c FROM ContactSubmission c WHERE c.isProcessed = false ORDER BY c.createdAt DESC")
    List<ContactSubmission> findUnprocessedSubmissions();
    
    @Query("SELECT c FROM ContactSubmission c WHERE c.createdAt >= :startDate AND c.createdAt <= :endDate ORDER BY c.createdAt DESC")
    List<ContactSubmission> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT c FROM ContactSubmission c WHERE c.email = :email AND c.message = :message AND c.createdAt >= :since")
    List<ContactSubmission> findRecentSubmissionsByEmailAndMessage(@Param("email") String email, @Param("message") String message, @Param("since") LocalDateTime since);
}
