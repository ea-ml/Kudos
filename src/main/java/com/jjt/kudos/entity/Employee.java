package com.jjt.kudos.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "employee_id", length = 255, unique = true)
    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    @Column(name = "name", length = 255, nullable = false)
    @NotBlank(message = "Name is required")
    private String name;

    @Column(name = "email", length = 255, unique = true, nullable = false)
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToMany(mappedBy = "members")
    private Set<Team> teams = new HashSet<>();

    @Column(name = "kudos_count")
    private Long kudosCount = 0L;

    public Department getDepartment() {
        return this.department;
    }

    public void setDepartment(Department department) { 
        this.department = department;
    }

    public void addTeam(Team team) {
        teams.add(team);
        team.addMember(this);
    }

    public void removeTeam(Team team) {
        teams.remove(team);
        team.removeMember(this);
    }

} 