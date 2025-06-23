package com.jjt.kudos.service.impl;

import com.jjt.kudos.entity.Employee;
import com.jjt.kudos.entity.Team;
import com.jjt.kudos.repository.EmployeeRepository;
import com.jjt.kudos.repository.TeamRepository;
import com.jjt.kudos.service.TeamService;
import com.jjt.kudos.dto.TeamDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TeamServiceImpl implements TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public List<Team> getAllTeams() {
        return teamRepository.findAllWithMembers();
    }

    @Override
    public Page<Team> getAllTeamsPaginated(Pageable pageable) {
        return teamRepository.findAllWithMembersPaginated(pageable);
    }

    @Override
    public Page<Team> searchTeamsPaginated(String query, Pageable pageable) {
        return teamRepository.searchTeamsPaginated(query, pageable);
    }

    @Override
    public Team getTeamById(Long id) {
        return teamRepository.findByIdWithMembers(id)
                .orElseThrow(() -> new RuntimeException("Team not found with id: " + id));
    }

    @Override
    public Team saveTeam(Team team) {
        return teamRepository.save(team);
    }

    @Override
    public void deleteTeam(Long id) {
        teamRepository.deleteById(id);
    }

    @Override
    public Team updateTeam(Team team) {
        return teamRepository.save(team);
    }

    @Override
    @Transactional
    public Team updateTeamMembers(Long teamId, Set<Long> memberIds) {
        Team team = getTeamById(teamId);
        
        // Create a new set of members to avoid concurrent modification
        Set<Employee> newMembers = new HashSet<>();
        
        // Add new members
        if (memberIds != null && !memberIds.isEmpty()) {
            for (Long employeeId : memberIds) {
                Employee employee = employeeRepository.findById(employeeId)
                        .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
                newMembers.add(employee);
            }
        }
        
        team.setMembers(newMembers);
        
        return teamRepository.save(team);
    }

    @Override
    public boolean existsByName(String name) {
        return teamRepository.existsByName(name);
    }

    @Override
    public TeamDTO createTeam(TeamDTO dto) {
        String name = HtmlUtils.htmlEscape(StringUtils.trimWhitespace(dto.getName()));
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Team name is required.");
        }
        if (existsByName(name)) {
            throw new IllegalArgumentException("A team with this name already exists.");
        }
        Team team = new Team();
        team.setName(name);
        // Set members if needed
        team = saveTeam(team);
        return new TeamDTO(team);
    }

    @Override
    public Team findByName(String name) {
        return teamRepository.findByName(name).orElse(null);
    }

    @Override
    @Transactional
    public TeamDTO updateTeam(Long id, TeamDTO teamDTO) {
        Team team = getTeamById(id);
        team.setName(teamDTO.getName());

        Set<Long> memberIds = teamDTO.getMemberIds() != null ? new HashSet<>(teamDTO.getMemberIds()) : new HashSet<>();
        Set<Employee> members = memberIds.stream()
                .map(employeeId -> employeeRepository.findById(employeeId)
                        .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId)))
                .collect(Collectors.toSet());
        team.setMembers(members);

        Team updatedTeam = updateTeam(team);
        return new TeamDTO(updatedTeam);
    }

    @Override
    public Page<TeamDTO> getTeams(String search, Pageable pageable) {
        Page<Team> teamPage;
        if (search != null && !search.trim().isEmpty()) {
            teamPage = searchTeamsPaginated(search.trim(), pageable);
        } else {
            teamPage = getAllTeamsPaginated(pageable);
        }
        return teamPage.map(TeamDTO::new);
    }

    @Override
    public List<Team> searchTeams(String query) {
        if (query == null || query.trim().isEmpty()) {
            return teamRepository.findAll();
        }
        // Use a custom query for searching teams by name
        return teamRepository.searchTeamsByName(query.trim());
    }
} 