package com.jjt.kudos.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee extends Recipient {

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
    @JsonIgnore
    private Set<Team> teams = new HashSet<>();

    @OneToMany(mappedBy = "sender")
    @JsonIgnore
    private Set<Kudos> sentKudos = new HashSet<>();

    public Department getDepartment() {
        return this.department;
    }

    public void setDepartment(Department department) { 
        this.department = department;
    }

    public void addTeam(Team team) {
        if (team != null && !teams.contains(team)) {
            teams.add(team);
            // Let Hibernate manage the bidirectional relationship
        }
    }

    public void removeTeam(Team team) {
        if (team != null && teams.contains(team)) {
            teams.remove(team);
            // Let Hibernate manage the bidirectional relationship
        }
    }

} 