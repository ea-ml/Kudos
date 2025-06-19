package com.jjt.kudos.service;

import com.jjt.kudos.entity.Employee;
import com.jjt.kudos.dto.EmployeeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface EmployeeService {
    List<Employee> getAllEmployees();
    Page<Employee> getAllEmployeesPaginated(Pageable pageable);
    Page<Employee> searchEmployeesPaginated(String query, Pageable pageable);
    Employee saveEmployee(Employee employee);
    Employee updateEmployee(Employee employee);
    void deleteEmployee(Long id);
    Employee getEmployeeById(Long id);
    List<Employee> searchEmployees(String query);
    boolean existsByEmail(String email);
    boolean existsByEmployeeId(String employeeId);
    boolean existsByName(String name);
    EmployeeDTO createEmployee(EmployeeDTO dto);
    List<Employee> getEmployeesByTeamId(Long teamId);
    int importEmployeesFromCsv(MultipartFile file) throws IOException;

    EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO);
    List<EmployeeDTO> searchEmployeesAsDTO(String query);
    Page<EmployeeDTO> getEmployees(String search, Pageable pageable);
} 