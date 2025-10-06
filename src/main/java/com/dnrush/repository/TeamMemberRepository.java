package com.dnrush.repository;

import com.dnrush.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    
    List<TeamMember> findByIsActiveTrueOrderBySortOrder();
    
    @Query("SELECT t FROM TeamMember t WHERE t.isActive = true ORDER BY t.sortOrder")
    List<TeamMember> findActiveMembers();
    
    @Query("SELECT t FROM TeamMember t LEFT JOIN FETCH t.avatarImage WHERE t.id = :id")
    Optional<TeamMember> findByIdWithAvatar(@Param("id") Long id);
}
