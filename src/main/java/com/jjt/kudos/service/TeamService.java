package com.jjt.kudos.service;

import com.jjt.kudos.entity.Team;
import com.jjt.kudos.dto.TeamDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface TeamService {
    List<Team> getAllTeams();
    Page<Team> getAllTeamsPaginated(Pageable pageable);
    Page<Team> searchTeamsPaginated(String query, Pageable pageable);
    Team getTeamById(Long id);
    Team saveTeam(Team team);
    void deleteTeam(Long id);
    Team updateTeam(Team team);
    Team updateTeamMembers(Long teamId, Set<Long> memberIds);
    boolean existsByName(String name);
    com.jjt.kudos.dto.TeamDTO createTeam(TeamDTO dto);
    Team findByName(String name);
    TeamDTO updateTeam(Long id, TeamDTO teamDTO);
    Page<TeamDTO> getTeams(String search, Pageable pageable);
} 