package com.jjt.kudos.service;

import com.jjt.kudos.entity.Department;
import java.util.List;

public interface DepartmentService {
    List<Department> getAllDepartments();
    Department getDepartmentById(Long id);
    Department saveDepartment(Department department);
    Department findByName(String name);
} 