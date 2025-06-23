package com.jjt.kudos.controller;

import com.jjt.kudos.dto.EmployeeDTO;
import com.jjt.kudos.dto.TopTeamDTO;
import com.jjt.kudos.dto.TeamDTO;
import com.jjt.kudos.dto.KudosDTO;
import com.jjt.kudos.entity.Department;
import com.jjt.kudos.entity.Employee;
import com.jjt.kudos.entity.Kudos;
import com.jjt.kudos.entity.Team;
import com.jjt.kudos.service.DepartmentService;
import com.jjt.kudos.service.EmployeeService;
import com.jjt.kudos.service.KudosService;
import com.jjt.kudos.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import com.jjt.kudos.service.FileuploadLogService;
import com.jjt.kudos.entity.User;
import com.jjt.kudos.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private KudosService kudosService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private FileuploadLogService fileuploadLogService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public String login() {
        return "admin/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Get first page of employees and teams for initial load
        Pageable employeePageable = PageRequest.of(0, 10, Sort.by("name"));
        Pageable teamPageable = PageRequest.of(0, 10, Sort.by("name"));
        
        Page<Employee> employeePage = employeeService.getAllEmployeesPaginated(employeePageable);
        Page<Team> teamPage = teamService.getAllTeamsPaginated(teamPageable);
        List<Kudos> kudosList = kudosService.getAllKudos();
        List<Department> departments = departmentService.getAllDepartments();
        
        model.addAttribute("employees", employeePage.getContent());
        model.addAttribute("employeePage", employeePage);
        model.addAttribute("kudosList", kudosList);
        model.addAttribute("departments", departments);
        model.addAttribute("allTeams", teamPage.getContent());
        model.addAttribute("teamPage", teamPage);
        
        return "admin/dashboard";
    }

    @GetMapping("/employees/paginated")
    @ResponseBody
    public Page<EmployeeDTO> getEmployeesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return employeeService.getEmployees(search, pageable);
    }

    @GetMapping("/teams/paginated")
    @ResponseBody
    public Page<TeamDTO> getTeamsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return teamService.getTeams(search, pageable);
    }

    @PutMapping(value = "/employees/{id}/reset-kudos", produces = "application/json")
    @ResponseBody
    public ResponseEntity resetKudosForEmployeeRest(@PathVariable Long id) {
        try {
            kudosService.resetKudosForEmployee(id);
            Employee employee = employeeService.getEmployeeById(id);
            return ResponseEntity.ok("All active kudos for " + employee.getName() + " have been reset");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to reset kudos. Please try again.");
        }
    }

    @GetMapping("/employees/{id}")
    @ResponseBody
    public EmployeeDTO getEmployee(@PathVariable Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        return new EmployeeDTO(employee);
    }

    @PostMapping("/employees/{id}/delete")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            employeeService.deleteEmployee(id);
            redirectAttributes.addFlashAttribute("success", "Employee deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete employee: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/employees/search")
    @ResponseBody
    public List<EmployeeDTO> searchEmployees(@RequestParam String query) {
        return employeeService.searchEmployeesAsDTO(query);
    }

    @PostMapping(value = "/employees", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> addEmployeeRest(@Valid @RequestBody EmployeeDTO employeeDTO) {
        try {
            EmployeeDTO created = employeeService.createEmployee(employeeDTO);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to add employee. Please try again.");
        }
    }

    @PutMapping(value = "/employees/{id}", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> updateEmployeeRest(@PathVariable Long id, @Valid @RequestBody EmployeeDTO employeeDTO) {
        try {
            EmployeeDTO updatedEmployee = employeeService.updateEmployee(id, employeeDTO);
            return ResponseEntity.ok(updatedEmployee);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update employee. Please try again.");
        }
    }

    @DeleteMapping(value = "/employees/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity deleteEmployeeRest(@PathVariable Long id) {
        try {
            employeeService.deleteEmployee(id);
            return ResponseEntity.ok("Employee deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete employee. Please try again.");
        }
    }

    @PostMapping(value = "/teams", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity addTeamRest(@Valid @RequestBody TeamDTO teamDTO) {
        try {
            TeamDTO created = teamService.createTeam(teamDTO);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to add team. Please try again.");
        }
    }

    @PutMapping(value = "/teams/{id}", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity updateTeamRest(@PathVariable Long id, @Valid @RequestBody TeamDTO teamDTO) {
        try {
            TeamDTO updatedTeam = teamService.updateTeam(id, teamDTO);
            return ResponseEntity.ok(updatedTeam);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update team. Please try again.");
        }
    }

    @DeleteMapping(value = "/teams/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity deleteTeamRest(@PathVariable Long id) {
        try {
            Team team = teamService.getTeamById(id);
            String teamName = team.getName();
            teamService.deleteTeam(id);
            return ResponseEntity.ok("Team '" + teamName + "' deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete team. Please try again.");
        }
    }

    @GetMapping("/teams/{id}")
    @ResponseBody
    public TeamDTO getTeam(@PathVariable Long id) {
        Team team = teamService.getTeamById(id);
        return new TeamDTO(team);
    }

    @GetMapping("/kudos/paginated")
    @ResponseBody
    public Page<com.jjt.kudos.repository.KudosRepository.AdminKudosProjection> getKudosPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return kudosService.searchAdminKudos(search != null ? search.trim() : "", pageable);
    }

    @GetMapping("/csv-upload")
    public String showCsvUploadPage() {
        return "admin/csv-upload";
    }

    @PostMapping("/csv-upload")
    public String handleCsvUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        // Get uploaderId from authenticated user
        Long uploaderId = null;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsernameOrEmail(username, username).orElse(null);
            if (user != null) {
                uploaderId = user.getId();
            }
        } catch (Exception ignored) {}

        if (uploaderId == null) {
            redirectAttributes.addFlashAttribute("error", "No authenticated user found. Please log in and try again.");
            return "redirect:/admin/csv-upload";
        }

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select a CSV file to upload.");
            fileuploadLogService.logUpload(uploaderId, "FAILURE", "No file selected", 0);
            return "redirect:/admin/csv-upload";
        }

        try {
            int count = employeeService.importEmployeesFromCsv(file);
            redirectAttributes.addFlashAttribute("success", count + " employees uploaded successfully.");
            fileuploadLogService.logUpload(uploaderId, "SUCCESS", null, count);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while processing the CSV file. " + e.getMessage());
            fileuploadLogService.logUpload(uploaderId, "FAILURE", e.getMessage(), 0);
        }

        return "redirect:/admin/csv-upload";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
} 