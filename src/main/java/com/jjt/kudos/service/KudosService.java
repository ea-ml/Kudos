package com.jjt.kudos.service;

import com.jjt.kudos.dto.TopEmployeeDTO;
import com.jjt.kudos.dto.TopTeamDTO;
import java.util.List;

public interface KudosService {
    List<TopEmployeeDTO> getTopEmployees(int limit);
    List<TopTeamDTO> getTopTeams(int limit);
} 