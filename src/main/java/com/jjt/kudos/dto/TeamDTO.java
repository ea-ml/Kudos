package com.jjt.kudos.dto;

import com.jjt.kudos.entity.Team;
import com.jjt.kudos.entity.Employee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.validation.constraints.Pattern;

public class TeamDTO {
    private Long id;
    @Pattern(regexp = "^[a-zA-Z0-9 -]*$", message = "Team name must only contain letters, numbers, spaces, and dashes")
    private String name;
    private List<EmployeeDTO> members;
    private List<Long> memberIds;
    private int memberCount;

    public TeamDTO() {}

    public TeamDTO(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        if (team.getMembers() != null) {
            this.members = team.getMembers().stream()
                    .map(EmployeeDTO::new)
                    .collect(Collectors.toList());
            this.memberCount = team.getMembers().size();
        } else {
            this.memberCount = 0;
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<EmployeeDTO> getMembers() {
        return members;
    }

    public List<Long> getMemberIds() {
        if (members == null) {
            if (!memberIds.isEmpty()) { 
                return memberIds;
            }
            else { 
                new ArrayList<>();
            }
        }
        return members.stream().map(EmployeeDTO::getId).collect(java.util.stream.Collectors.toList());
    }

    public void setMembers(List<EmployeeDTO> members) {
        this.members = members;
        this.memberCount = members != null ? members.size() : 0;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }
} 