package com.jjt.kudos.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

import com.jjt.kudos.entity.Employee;
import com.jjt.kudos.entity.Recipient;

@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Team extends Recipient {

    @Column(name = "name", length = 255, unique = true, nullable = false)
    @NotBlank(message = "Team name is required")
    private String name;

    @ManyToMany
    @JoinTable(
        name = "team_employees",
        joinColumns = @JoinColumn(name = "team_id"),
        inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    @JsonIgnore
    private Set<Employee> members = new HashSet<>();

    // Helper methods to manage the bidirectional relationship
    public void addMember(Employee employee) {
        if (employee != null) {
            members.add(employee);
            employee.addTeam(this);
        }
    }

    public void removeMember(Employee employee) {
        if (employee != null) {
            members.remove(employee);
            employee.removeTeam(this);
        }
    }

    public void setMembers(Set<Employee> employees) {
        // Clear existing relationships
        if (members != null) {
            for (Employee existingMember : new HashSet<>(members)) {
                removeMember(existingMember);
            }
        }
        
        // Add new relationships
        if (employees != null) {
            for (Employee employee : employees) {
                addMember(employee);
            }
        }
    }
} 