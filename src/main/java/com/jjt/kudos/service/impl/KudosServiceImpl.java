package com.jjt.kudos.service.impl;

import com.jjt.kudos.dto.TopEmployeeDTO;
import com.jjt.kudos.dto.TopTeamDTO;
import com.jjt.kudos.repository.EmployeeRepository;
import com.jjt.kudos.repository.TeamRepository;
import com.jjt.kudos.service.KudosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KudosServiceImpl implements KudosService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Override
    public List<TopEmployeeDTO> getTopEmployees(int limit) {
        return employeeRepository.findTopEmployeesByKudosCount(limit)
                .stream()
                .map(employee -> {
                    TopEmployeeDTO dto = new TopEmployeeDTO();
                    dto.setName(employee.getName());
                    dto.setDepartment(employee.getDepartment() != null ? employee.getDepartment().getName() : null);
                    dto.setKudosCount(employee.getKudosCount());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<TopTeamDTO> getTopTeams(int limit) {
        return teamRepository.findTopTeamsByKudosCount(limit)
                .stream()
                .map(team -> {
                    TopTeamDTO dto = new TopTeamDTO();
                    dto.setName(team.getName());
                    dto.setMemberCount(team.getMembers().size());
                    dto.setKudosCount(team.getKudosCount());
                    return dto;
                })
                .collect(Collectors.toList());
    }
} 