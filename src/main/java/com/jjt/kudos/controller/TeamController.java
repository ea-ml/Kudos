package com.jjt.kudos.controller;

import com.jjt.kudos.entity.Employee;
import com.jjt.kudos.dto.TopTeamDTO;
import com.jjt.kudos.dto.TopEmployeeDTO;
import com.jjt.kudos.service.KudosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;
import com.jjt.kudos.service.EmployeeService;

import java.util.List;

@Controller
public class TeamController {
    @Autowired
    private KudosService kudosService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/teams/ranking")
    public String teamRankingPage() {
        return "team-ranking";
    }

    @GetMapping("/teams/ranking/paginated")
    @ResponseBody
    public Page<TopTeamDTO> getTeamRankingPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<TopTeamDTO> all = kudosService.getTopTeams(Integer.MAX_VALUE);
        all.sort(java.util.Comparator.comparingLong(TopTeamDTO::getKudosCount).reversed());
        int start = Math.min(page * size, all.size());
        int end = Math.min(start + size, all.size());
        List<TopTeamDTO> content = all.subList(start, end);
        return new PageImpl<>(content, PageRequest.of(page, size), all.size());
    }

    @GetMapping("/teams/{id}/kudos")
    public String teamKudosPage(@PathVariable Long id, Model model) {
        var team = kudosService.getTopTeams(Integer.MAX_VALUE).stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);
        model.addAttribute("entityType", "team");
        model.addAttribute("entityName", team != null ? team.getName() : "Team");
        model.addAttribute("entityId", id);
        return "entity-kudos";
    }

    @GetMapping("/teams/{id}/members/leaderboard")
    @ResponseBody
    public Page<TopEmployeeDTO> getTeamMembersLeaderboard(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Employee> all = employeeService.getEmployeesByTeamId(id);
        int start = Math.min(page * size, all.size());
        int end = Math.min(start + size, all.size());
        List<TopEmployeeDTO> content = all.subList(start, end).stream().map(e -> {
            TopEmployeeDTO dto = new TopEmployeeDTO();
            dto.setName(e.getName());
            dto.setDepartment(e.getDepartment() != null ? e.getDepartment().getName() : "");
            dto.setKudosCount((long) e.getSentKudos().stream().filter(k -> k.getRecipient().equals(e) && k.isActive()).count());
            return dto;
        }).toList();
        return new PageImpl<>(content, PageRequest.of(page, size), all.size());
    }

    @GetMapping("/teams/{id}/members/leaderboard-page")
    public String teamMemberLeaderboardPage(@PathVariable Long id, Model model) {
        var team = kudosService.getTopTeams(Integer.MAX_VALUE).stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);
        model.addAttribute("teamId", id);
        model.addAttribute("teamName", team != null ? team.getName() : "Team");
        return "team-member-leaderboard";
    }
} 