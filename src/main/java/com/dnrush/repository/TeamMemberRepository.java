package com.dnrush.repository;

import com.dnrush.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    
    List<TeamMember> findByIsActiveTrueOrderBySortOrder();
    
    @Query("SELECT t FROM TeamMember t WHERE t.isActive = true ORDER BY t.sortOrder")
    List<TeamMember> findActiveMembers();
}
