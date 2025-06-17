package com.jjt.kudos.config;

import com.jjt.kudos.entity.Department;
import com.jjt.kudos.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private DepartmentRepository departmentRepository;

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
    }
} 