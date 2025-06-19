package com.jjt.kudos.repository;

import com.jjt.kudos.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    @Query("SELECT e FROM Employee e LEFT JOIN Kudos k ON e = k.recipient AND k.isActive = true GROUP BY e ORDER BY COUNT(k) DESC")
    List<Employee> findTopEmployeesByKudosCount(Pageable pageable);
    
    boolean existsByEmployeeId(String employeeId);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT e FROM Employee e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(e.employeeId) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY e.name")
    List<Employee> searchEmployees(@Param("query") String query);
    
    @Query("SELECT e FROM Employee e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(e.employeeId) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Employee> searchEmployeesPaginated(@Param("query") String query, Pageable pageable);
    
    @Query("SELECT e FROM Employee e JOIN e.teams t LEFT JOIN Kudos k ON k.recipient = e AND k.isActive = true WHERE t.id = :teamId GROUP BY e ORDER BY COUNT(k) DESC")
    List<Employee> findByTeamId(@Param("teamId") Long teamId);
} 