package com.jjt.kudos.dto;

import com.jjt.kudos.entity.Employee;
import com.jjt.kudos.entity.Department;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public class EmployeeDTO {
    private Long id;

    @Pattern(regexp = "^[0-9 -]*$", message = "Employee ID must only contain numbers, spaces, and dashes")
    private String employeeId;

    @Pattern(regexp = "^[a-zA-Z ]*$", message = "Name must only contain letters and spaces")
    private String name;

    @Email(message = "Email should be a valid email address")
    private String email;
    private Long departmentId;
    private DepartmentDTO department;

    public EmployeeDTO() {}

    public EmployeeDTO(Employee employee) {
        this.id = employee.getId();
        this.employeeId = employee.getEmployeeId();
        this.name = employee.getName();
        this.email = employee.getEmail();
        if (employee.getDepartment() != null) {
            this.department = new DepartmentDTO(employee.getDepartment());
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDepartmentId() {
        return department != null
            ? department.getId()
            : departmentId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public DepartmentDTO getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentDTO department) {
        this.department = department;
    }

    // Inner class for Department
    public static class DepartmentDTO {
        private Long id;
        private String name;

        public DepartmentDTO() {}

        public DepartmentDTO(Department department) {
            this.id = department.getId();
            this.name = department.getName();
        }

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
    }
} 