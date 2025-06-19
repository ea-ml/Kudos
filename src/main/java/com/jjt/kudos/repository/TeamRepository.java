package com.jjt.kudos.repository;

import com.jjt.kudos.entity.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    
    @Query("SELECT DISTINCT t FROM Team t LEFT JOIN FETCH t.members")
    List<Team> findAllWithMembers();
    
    @Query("SELECT t FROM Team t LEFT JOIN FETCH t.members WHERE t.id = ?1")
    Optional<Team> findByIdWithMembers(Long id);
    
    @Query("SELECT t FROM Team t LEFT JOIN FETCH t.members")
    Page<Team> findAllWithMembersPaginated(Pageable pageable);
    
    @Query("SELECT t FROM Team t LEFT JOIN FETCH t.members WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Team> searchTeamsPaginated(@Param("query") String query, Pageable pageable);

    boolean existsByName(String name);
    
    Optional<Team> findByName(String name);
} 