package com.jjt.kudos.service.impl;

import com.jjt.kudos.entity.Employee;
import com.jjt.kudos.dto.EmployeeDTO;
import com.jjt.kudos.entity.Department;
import com.jjt.kudos.entity.Team;
import com.jjt.kudos.repository.DepartmentRepository;
import com.jjt.kudos.repository.EmployeeRepository;
import com.jjt.kudos.service.DepartmentService;
import com.jjt.kudos.service.EmployeeService;
import com.jjt.kudos.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;
import org.springframework.util.StringUtils;
import com.jjt.kudos.entity.Kudos;
import com.jjt.kudos.repository.KudosRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.IllegalArgumentException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private KudosRepository kudosRepository;

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Page<Employee> getAllEmployeesPaginated(Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }

    @Override
    public Page<Employee> searchEmployeesPaginated(String query, Pageable pageable) {
        return employeeRepository.searchEmployeesPaginated(query, pageable);
    }

    @Override
    @Transactional
    public Employee saveEmployee(Employee employee) {
        if (employeeRepository.existsByEmployeeId(employee.getEmployeeId())) {
            throw new RuntimeException("Employee ID already exists");
        }
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        return employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public Employee updateEmployee(Employee employee) {
        Employee existingEmployee = employeeRepository.findById(employee.getId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Check if the new employee ID is different and already exists
        if (!existingEmployee.getEmployeeId().equals(employee.getEmployeeId()) &&
            employeeRepository.existsByEmployeeId(employee.getEmployeeId())) {
            throw new RuntimeException("Employee ID already exists");
        }

        // Check if the new email is different and already exists
        if (!existingEmployee.getEmail().equals(employee.getEmail()) &&
            employeeRepository.existsByEmail(employee.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        return employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        employee.removeFromAllTeams();
        kudosRepository.deleteAllBySenderOrRecipient(employee.getId());
        employeeRepository.delete(employee);
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    @Override
    public List<Employee> searchEmployees(String query) {
        if (query == null || query.trim().isEmpty()) {
            return employeeRepository.findAll();
        }
        return employeeRepository.searchEmployees(query.trim());
    }

    @Override
    public boolean existsByEmail(String email) {
        return employeeRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByEmployeeId(String employeeId) {
        return employeeRepository.existsByEmployeeId(employeeId);
    }

    @Override
    public boolean existsByName(String name) {
        return employeeRepository.findAll().stream().anyMatch(e -> e.getName().equalsIgnoreCase(name));
    }

    @Override
    public EmployeeDTO createEmployee(EmployeeDTO dto) {
        String employeeId = HtmlUtils.htmlEscape(StringUtils.trimWhitespace(dto.getEmployeeId()));
        String name = HtmlUtils.htmlEscape(StringUtils.trimWhitespace(dto.getName()));
        String email = HtmlUtils.htmlEscape(StringUtils.trimWhitespace(dto.getEmail()));
        if (employeeId.isEmpty() || name.isEmpty() || email.isEmpty()) {
            throw new IllegalArgumentException("All fields are required.");
        }
        if (existsByEmail(email)) {
            throw new IllegalArgumentException("An employee with this email already exists.");
        }
        if (existsByEmployeeId(employeeId)) {
            throw new IllegalArgumentException("An employee with this employee number already exists.");
        }
        if (existsByName(name)) {
            throw new IllegalArgumentException("An employee with this name already exists.");
        }
        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);
        employee.setName(name);
        employee.setEmail(email);
        employee.setDepartment(dto.getDepartmentId() != null ? departmentRepository.findById(dto.getDepartmentId()).orElse(null) : null);
        employee = saveEmployee(employee);
        return new EmployeeDTO(employee);
    }

    @Override
    public List<Employee> getEmployeesByTeamId(Long teamId) {
        return employeeRepository.findByTeamId(teamId);
    }

    @Override
    @Transactional
    public int importEmployeesFromCsv(MultipartFile file) throws IOException {
        List<String[]> allRows = new java.util.ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), java.nio.charset.StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IllegalArgumentException("Invalid CSV found");
            }
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length < 5) continue;
                allRows.add(values);
            }
        }

        for (int i = 0; i < allRows.size(); i++) {
            String[] values = allRows.get(i);
            int rowNum = i + 1;
            String email = values[0].trim();
            String name = values[1].trim();
            String employeeId = values[2].trim();
            String departmentName = values[3].trim();
            String teamNames = values[4].trim();

            if (email.isEmpty() || name.isEmpty() || employeeId.isEmpty() || departmentName.isEmpty()) {
                throw new IllegalArgumentException("Invalid input: missing required field at row " + rowNum);
            }
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                throw new IllegalArgumentException("Invalid email \"" + email + "\" format at row " + rowNum);
            }
            if (!employeeId.matches("^[0-9 -]*$")) {
                throw new IllegalArgumentException("Invalid employee ID format at row " + rowNum);
            }
            if (!name.matches("^[a-zA-Z ]*$")) {
                throw new IllegalArgumentException("Invalid name format at row " + rowNum);
            }
            if (existsByEmail(email)) {
                throw new IllegalArgumentException("Email " + email + " already exists at row " + rowNum);
            }
            if (existsByEmployeeId(employeeId)) {
                throw new IllegalArgumentException("Employee ID " + employeeId +" already exists at row " + rowNum);
            }
            Department department = departmentService.findByName(departmentName);
            if (department == null) {
                throw new IllegalArgumentException("Department " + departmentName + " not found at row " + rowNum);
            }
            if (!teamNames.isEmpty()) {
                String[] teamArr = teamNames.split("\\s*,\\s*");
                for (String teamName : teamArr) {
                    if (teamService.findByName(teamName) == null) {
                        throw new IllegalArgumentException("Team not found: " + teamName + " at row " + rowNum);
                    }
                }
            }
        }
        // If all rows are valid, create employees
        int count = 0;
        for (String[] values : allRows) {
            String email = values[0].trim();
            String name = values[1].trim();
            String employeeId = values[2].trim();
            String departmentName = values[3].trim();
            String teamNames = values[4].trim();
            Department department = departmentService.findByName(departmentName);
            EmployeeDTO dto = new EmployeeDTO();
            dto.setEmail(email);
            dto.setName(name);
            dto.setEmployeeId(employeeId);
            dto.setDepartment(new EmployeeDTO.DepartmentDTO(department));
            EmployeeDTO created = createEmployee(dto);
            if (!teamNames.isEmpty()) {
                String[] teamArr = teamNames.split("\\s*,\\s*");
                Employee employee = getEmployeeById(created.getId());
                for (String teamName : teamArr) {
                    Team team = teamService.findByName(teamName);
                    team.getMembers().add(employee);
                    teamService.saveTeam(team);
                }
            }
            count++;
        }
        return count;
    }

    @Override
    @Transactional
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO) {
        Employee employee = getEmployeeById(id);
        employee.setEmployeeId(employeeDTO.getEmployeeId());
        employee.setName(employeeDTO.getName());
        employee.setEmail(employeeDTO.getEmail());
        Department department = departmentService.getDepartmentById(employeeDTO.getDepartment().getId());
        employee.setDepartment(department);
        employee = updateEmployee(employee);
        return new EmployeeDTO(employee);
    }

    @Override
    public List<EmployeeDTO> searchEmployeesAsDTO(String query) {
        return searchEmployees(query).stream()
                .map(EmployeeDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public Page<EmployeeDTO> getEmployees(String search, Pageable pageable) {
        Page<Employee> employeePage;
        if (search != null && !search.trim().isEmpty()) {
            employeePage = searchEmployeesPaginated(search.trim(), pageable);
        } else {
            employeePage = getAllEmployeesPaginated(pageable);
        }
        return employeePage.map(EmployeeDTO::new);
    }
} 