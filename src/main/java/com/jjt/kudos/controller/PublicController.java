package com.jjt.kudos.controller;

import com.jjt.kudos.dto.EmployeeDTO;
import com.jjt.kudos.dto.TeamDTO;
import com.jjt.kudos.dto.KudosDTO;
import com.jjt.kudos.service.EmployeeService;
import com.jjt.kudos.service.TeamService;
import com.jjt.kudos.service.KudosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.jjt.kudos.entity.Kudos;
import java.util.HashMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class PublicController {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private KudosService kudosService;

    @GetMapping("/employees/all")
    public List<EmployeeDTO> getAllEmployees() {
        return employeeService.getAllEmployees().stream()
                .map(EmployeeDTO::new)
                .toList();
    }

    @GetMapping("/teams/all")
    public List<TeamDTO> getAllTeams() {
        return teamService.getAllTeams().stream()
                .map(TeamDTO::new)
                .toList();
    }

    @PostMapping("/kudos")
    public ResponseEntity<?> createKudos(@RequestBody KudosDTO kudosDTO) {
        try {
            KudosDTO created = kudosService.createKudosWithValidation(kudosDTO);
        return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to send kudos. Please try again.");
        }
    }

    @GetMapping("/kudos")
    @ResponseBody
    public ResponseEntity<?> getKudosForEntity(@RequestParam String type, @RequestParam Long id) {
        try {
            return ResponseEntity.ok(kudosService.getKudosForEntity(type, id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to fetch kudos. Please try again.");
        }
    }

    private Map<String, Object> kudosToMap(Kudos k) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", k.getMessage());
        map.put("senderName", k.isAnonymous() ? "Anonymous" : k.getSender().getName());
        map.put("isAnonymous", k.isAnonymous());
        map.put("createdAt", k.getCreatedAt());
        return map;
    }
} 