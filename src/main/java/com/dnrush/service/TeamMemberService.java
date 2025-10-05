package com.dnrush.service;

import com.dnrush.entity.TeamMember;
import com.dnrush.repository.TeamMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamMemberService {
    
    @Autowired
    private TeamMemberRepository teamMemberRepository;
    
    public List<TeamMember> getActiveMembers() {
        return teamMemberRepository.findActiveMembers();
    }
    
    public TeamMember saveMember(TeamMember teamMember) {
        return teamMemberRepository.save(teamMember);
    }
    
    public void deleteMember(Long id) {
        teamMemberRepository.deleteById(id);
    }
    
    public TeamMember getMemberById(Long id) {
        return teamMemberRepository.findById(id).orElse(null);
    }
}
