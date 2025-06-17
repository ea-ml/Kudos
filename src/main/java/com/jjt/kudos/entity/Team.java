package com.jjt.kudos.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "teams")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", length = 255, unique = true, nullable = false)
    @NotBlank(message = "Team name is required")
    private String name;

    @ManyToMany
    @JoinTable(
        name = "team_employees",
        joinColumns = @JoinColumn(name = "team_id"),
        inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private Set<Employee> members = new HashSet<>();

    @Column(name = "kudos_count")
    private Long kudosCount = 0L;

    public void addMember(Employee employee) {
        members.add(employee);
    }

    public void removeMember(Employee employee) {
        members.remove(employee);
    }
} 