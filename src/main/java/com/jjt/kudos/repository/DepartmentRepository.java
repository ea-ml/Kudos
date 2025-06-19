package com.jjt.kudos.repository;

import com.jjt.kudos.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsByName(String name);
    java.util.Optional<Department> findByName(String name);
} 