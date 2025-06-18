package com.jjt.kudos.dto;

import lombok.Data;

@Data
public class TopTeamDTO {
    private Long id;
    private String name;
    private Integer memberCount;
    private Integer kudosCount;
} 