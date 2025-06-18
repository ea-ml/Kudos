package com.jjt.kudos.config;

import com.jjt.kudos.entity.Department;
import com.jjt.kudos.entity.RecipientType;
import com.jjt.kudos.repository.DepartmentRepository;
import com.jjt.kudos.repository.RecipientTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private RecipientTypeRepository recipientTypeRepository;

    @Override
    public void run(String... args) {
        // Create Operations department if it doesn't exist
        if (!departmentRepository.existsByName("Operations")) {
            Department operations = new Department();
            operations.setName("Operations");
            departmentRepository.save(operations);
        }

        // Create Support department if it doesn't exist
        if (!departmentRepository.existsByName("Support")) {
            Department support = new Department();
            support.setName("Support");
            departmentRepository.save(support);
        }

        // Seed recipient types
        if (recipientTypeRepository.findAll().stream().noneMatch(rt -> "employee".equalsIgnoreCase(rt.getName()))) {
            RecipientType employeeType = new RecipientType();
            employeeType.setName("employee");
            recipientTypeRepository.save(employeeType);
        }
        if (recipientTypeRepository.findAll().stream().noneMatch(rt -> "team".equalsIgnoreCase(rt.getName()))) {
            RecipientType teamType = new RecipientType();
            teamType.setName("team");
            recipientTypeRepository.save(teamType);
        }
    }
} 