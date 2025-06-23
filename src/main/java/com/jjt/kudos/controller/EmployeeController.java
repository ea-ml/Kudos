package com.jjt.kudos.controller;

import com.jjt.kudos.entity.Team;
import com.jjt.kudos.entity.Employee;
import com.jjt.kudos.dto.EmployeeDTO;
import com.jjt.kudos.dto.TeamDTO;
import com.jjt.kudos.dto.TopEmployeeDTO;
import com.jjt.kudos.dto.TopTeamDTO;
import com.jjt.kudos.service.EmployeeService;
import com.jjt.kudos.service.TeamService;
import com.jjt.kudos.service.KudosService;
import com.jjt.kudos.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.util.Comparator;
import org.springframework.data.domain.Sort;
import com.jjt.kudos.entity.Kudos;
import com.jjt.kudos.dto.KudosDTO;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private KudosService kudosService;

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/")
    public String home(Model model) {
        List<TopEmployeeDTO> topEmployees = kudosService.getTopEmployees(5);
        List<TopTeamDTO> topTeams = kudosService.getTopTeams(5);
        List<com.jjt.kudos.entity.Department> departments = departmentService.getAllDepartments();
        model.addAttribute("topEmployees", topEmployees);
        model.addAttribute("topTeams", topTeams);
        model.addAttribute("departments", departments);
        return "home";
    }

    @GetMapping("/employees/ranking")
    public String employeeRankingPage() {
        return "employee-ranking";
    }

    @GetMapping("/employees/ranking/paginated")
    @ResponseBody
    public Page<TopEmployeeDTO> getEmployeeRankingPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<TopEmployeeDTO> all = kudosService.getTopEmployees(Integer.MAX_VALUE);
        all.sort(Comparator.comparingLong(TopEmployeeDTO::getKudosCount).reversed());
        int start = Math.min(page * size, all.size());
        int end = Math.min(start + size, all.size());
        List<TopEmployeeDTO> content = all.subList(start, end);
        return new PageImpl<>(content, PageRequest.of(page, size), all.size());
    }

    @GetMapping("/search/entities")
    @ResponseBody
    public Map<String, List<Map<String, Object>>> searchEntities(@RequestParam String query) {
        List<Employee> employees = employeeService.searchEmployees(query);
        List<Map<String, Object>> employeeResults = employees.stream()
            .map(e -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", e.getId());
                map.put("name", e.getName());
                return map;
            })
            .toList();

        List<Team> teams = teamService.searchTeams(query);
        List<Map<String, Object>> teamResults = teams.stream()
            .map(t -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", t.getId());
                map.put("name", t.getName());
                return map;
            })
            .toList();

        HashMap<String, List<Map<String, Object>>> result = new HashMap<>();
        result.put("employees", employeeResults);
        result.put("teams", teamResults);
        return result;
    }

    @GetMapping("/employees/{id}/kudos")
    public String employeeKudosPage(@PathVariable Long id, org.springframework.ui.Model model) {
        var employee = employeeService.getEmployeeById(id);
        model.addAttribute("entityType", "employee");
        model.addAttribute("entityName", employee.getName());
        model.addAttribute("entityId", id);
        return "entity-kudos";
    }

    @GetMapping("/employees/top-by-department")
    @ResponseBody
    public List<TopEmployeeDTO> getTopEmployeesByDepartment(@RequestParam(required = false) String department) {
        List<TopEmployeeDTO> all = kudosService.getTopEmployees(Integer.MAX_VALUE);
        if (department != null && !department.isEmpty()) {
            all = all.stream()
                .filter(e -> department.equals(e.getDepartment()))
                .sorted(Comparator.comparingLong(TopEmployeeDTO::getKudosCount).reversed())
                .limit(5)
                .toList();
        } else {
            all = all.stream()
                .sorted(Comparator.comparingLong(TopEmployeeDTO::getKudosCount).reversed())
                .limit(5)
                .toList();
        }
        return all;
    }

    @GetMapping("/kudos/paginated")
    @ResponseBody
    public Page<KudosDTO> getPublicKudosPaginated(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Page<Kudos> kudosPage = kudosService.getAllKudos(PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return kudosPage.map(k -> {
            String senderName = k.isAnonymous() ? "Anonymous" : (k.getSender() != null ? k.getSender().getName() : "");
            String recipientName = "";
            if (k.getRecipient() != null) {
                if (k.getRecipient() instanceof Employee emp) {
                    recipientName = emp.getName();
                } else if (k.getRecipient() instanceof Team team) {
                    recipientName = team.getName();
                }
            }
            return new KudosDTO(
                k.getId(),
                k.getSender() != null ? k.getSender().getId() : null,
                k.getRecipient() != null ? k.getRecipient().getId() : null,
                k.getMessage(),
                k.isActive(),
                k.isAnonymous(),
                k.getCreatedAt(),
                senderName,
                recipientName
            );
        });
    }
} 