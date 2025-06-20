package com.jjt.kudos.service.impl;

import com.jjt.kudos.dto.TopEmployeeDTO;
import com.jjt.kudos.dto.TopTeamDTO;
import com.jjt.kudos.dto.KudosDTO;
import com.jjt.kudos.entity.Kudos;
import com.jjt.kudos.entity.Team;
import com.jjt.kudos.entity.Employee;
import com.jjt.kudos.entity.Recipient;
import com.jjt.kudos.entity.RecipientType;
import com.jjt.kudos.repository.EmployeeRepository;
import com.jjt.kudos.repository.KudosRepository;
import com.jjt.kudos.repository.TeamRepository;
import com.jjt.kudos.repository.RecipientTypeRepository;
import com.jjt.kudos.service.KudosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;
import org.springframework.util.StringUtils;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Collections;

@Service
public class KudosServiceImpl implements KudosService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private KudosRepository kudosRepository;

    @Autowired
    private RecipientTypeRepository recipientTypeRepository;

    @Override
    public List<TopEmployeeDTO> getTopEmployees(int limit) {
        return employeeRepository.findTopEmployeesByKudosCount(PageRequest.of(0, limit))
                .stream()
                .map(employee -> {
                    TopEmployeeDTO dto = new TopEmployeeDTO();
                    dto.setName(employee.getName());
                    dto.setDepartment(employee.getDepartment() != null ? employee.getDepartment().getName() : null);
                    // Direct kudos
                    long directKudos = kudosRepository.countActiveKudosByRecipientId(employee.getId());
                    // Team kudos 
                    long teamKudos = employee.getTeams() == null ? 0 : employee.getTeams().stream()
                        .mapToLong(team -> kudosRepository.countActiveKudosByTeamRecipientId(team.getId()))
                        .sum();
                    dto.setKudosCount(directKudos + teamKudos);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<TopTeamDTO> getTopTeams(int limit) {
        return teamRepository.findAll()
                .stream()
                .limit(limit)
                .map(team -> {
                    TopTeamDTO dto = new TopTeamDTO();
                    dto.setId(team.getId());
                    dto.setName(team.getName());
                    dto.setMemberCount(team.getMembers().size());
                    int teamKudosCount = kudosRepository.countActiveKudosByTeamRecipientId(team.getId()).intValue();
                    dto.setKudosCount(teamKudosCount);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Kudos> getAllKudos() {
        return kudosRepository.findAll();
    }

    @Override
    public Page<Kudos> getAllKudos(Pageable pageable) {
        return kudosRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public void resetKudosForEmployee(Long employeeId) {
        List<Kudos> activeKudos = kudosRepository.findByRecipientIdAndIsActiveTrue(employeeId);
        for (Kudos kudos : activeKudos) {
            kudos.setActive(false);
        }
        kudosRepository.saveAll(activeKudos);
    }

    @Override
    @Transactional
    public KudosDTO createKudos(KudosDTO kudosDTO) {
        Employee sender = employeeRepository.findById(kudosDTO.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        
        Recipient recipient = employeeRepository.findById(kudosDTO.getRecipientId())
                .orElse(null);
        if (recipient == null) {
            recipient = teamRepository.findById(kudosDTO.getRecipientId())
                    .orElseThrow(() -> new IllegalArgumentException("Recipient not found as employee or team"));
        }

        Kudos kudos = new Kudos();
        kudos.setSender(sender);
        kudos.setRecipient(recipient);
        kudos.setMessage(kudosDTO.getMessage());
        kudos.setActive(true);
        kudos.setAnonymous(kudosDTO.isAnonymous());
        Kudos saved = kudosRepository.save(kudos);

        return new KudosDTO(
                saved.getId(),
                saved.getSender().getId(),
                saved.getRecipient().getId(),
                saved.getMessage(),
                saved.isActive(),
                saved.isAnonymous(),
                saved.getCreatedAt()
        );
    }

    @Override
    public org.springframework.data.domain.Page<com.jjt.kudos.repository.KudosRepository.AdminKudosProjection> searchAdminKudos(String search, org.springframework.data.domain.Pageable pageable) {
        return kudosRepository.searchAdminKudos(search, pageable);
    }

    @Override
    public boolean existsBySenderIdAndRecipientIdAndCreatedAtToday(Long senderId, Long recipientId) {
        return kudosRepository.existsBySenderIdAndRecipientIdAndCreatedAtToday(senderId, recipientId);
    }

    @Override
    public KudosDTO createKudosWithValidation(KudosDTO dto) {
        String message = HtmlUtils.htmlEscape(StringUtils.trimWhitespace(dto.getMessage()));
        if (message.isEmpty()) {
            throw new IllegalArgumentException("Kudos message is required.");
        }
        if (existsBySenderIdAndRecipientIdAndCreatedAtToday(dto.getSenderId(), dto.getRecipientId())) {
            throw new IllegalArgumentException("You have already sent kudos to this recipient today.");
        }
        dto.setMessage(message);
        return createKudos(dto);
    }

    @Override
    public Map<String, List<Map<String, Object>>> getKudosForEntity(String type, Long id) {
        if ("employee".equalsIgnoreCase(type)) {
            // Direct kudos
            List<Kudos> directKudosList = getAllKudos().stream()
                .filter(k -> k.getRecipient() instanceof Employee && k.getRecipient().getId().equals(id))
                .collect(Collectors.toList());

            // Team kudos
            Employee employee = employeeRepository.findById(id).orElse(null);
            List<Kudos> teamKudosList = employee != null ? employee.getTeams().stream()
                .flatMap(team -> getAllKudos().stream()
                    .filter(k -> k.getRecipient() instanceof Team && k.getRecipient().getId().equals(team.getId())))
                .collect(Collectors.toList()) : Collections.emptyList();

            return Map.of(
                "directKudos", directKudosList.stream().map(this::kudosToMap).collect(Collectors.toList()),
                "teamKudos", teamKudosList.stream().map(this::kudosToMap).collect(Collectors.toList())
            );
        } else { // Team
            List<Kudos> kudosList = getAllKudos().stream()
                .filter(k -> k.getRecipient() instanceof Team && k.getRecipient().getId().equals(id))
                .collect(Collectors.toList());
            return Map.of(
                "directKudos", kudosList.stream().map(this::kudosToMap).collect(Collectors.toList()),
                "teamKudos", Collections.emptyList()
            );
        }
    }

    private Map<String, Object> kudosToMap(Kudos k) {
        Map<String, Object> map = new java.util.HashMap<>();
        map.put("message", k.getMessage());
        map.put("senderName", k.isAnonymous() ? "Anonymous" : k.getSender().getName());
        map.put("isAnonymous", k.isAnonymous());
        map.put("createdAt", k.getCreatedAt());
        return map;
    }
} 