package com.jjt.kudos.controller;

import com.jjt.kudos.dto.TopEmployeeDTO;
import com.jjt.kudos.dto.TopTeamDTO;
import com.jjt.kudos.service.KudosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private KudosService kudosService;

    @GetMapping("/")
    public String home(Model model) {
        List<TopEmployeeDTO> topEmployees = kudosService.getTopEmployees(5);
        List<TopTeamDTO> topTeams = kudosService.getTopTeams(5);
        
        model.addAttribute("topEmployees", topEmployees);
        model.addAttribute("topTeams", topTeams);
        
        return "home";
    }
} 