package com.jjt.kudos.service;

import com.jjt.kudos.dto.TopEmployeeDTO;
import com.jjt.kudos.dto.TopTeamDTO;
import com.jjt.kudos.entity.Kudos;
import com.jjt.kudos.dto.KudosDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Map;

public interface KudosService {
    List<TopEmployeeDTO> getTopEmployees(int limit);
    List<TopTeamDTO> getTopTeams(int limit);
    List<Kudos> getAllKudos();
    void resetKudosForEmployee(Long employeeId);
    KudosDTO createKudos(KudosDTO kudosDTO);
    Page<Kudos> getAllKudos(Pageable pageable);
    org.springframework.data.domain.Page<com.jjt.kudos.repository.KudosRepository.AdminKudosProjection> searchAdminKudos(String search, org.springframework.data.domain.Pageable pageable);
    boolean existsBySenderIdAndRecipientIdAndCreatedAtToday(Long senderId, Long recipientId);
    KudosDTO createKudosWithValidation(KudosDTO dto);
    Map<String, List<Map<String, Object>>> getKudosForEntity(String type, Long id);
} 