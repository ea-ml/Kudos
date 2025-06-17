package com.jjt.kudos.repository;

import com.jjt.kudos.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    
    @Query("SELECT t FROM Team t ORDER BY t.kudosCount DESC")
    List<Team> findTopTeamsByKudosCount(int limit);
} 