package com.jjt.kudos.repository;

import com.jjt.kudos.entity.Kudos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KudosRepository extends JpaRepository<Kudos, Long> {
    
    List<Kudos> findByRecipientIdAndIsActiveTrue(Long recipientId);
    
    List<Kudos> findBySenderIdAndIsActiveTrue(Long senderId);
    
    @Query("SELECT COUNT(k) FROM Kudos k WHERE k.recipient.id = ?1 AND TYPE(k.recipient) = Employee AND k.isActive = true")
    Long countActiveKudosByRecipientId(Long recipientId);
    
    @Query("SELECT COUNT(k) FROM Kudos k WHERE k.sender.id = ?1 AND k.isActive = true")
    Long countActiveKudosBySenderId(Long senderId);

    @Query("SELECT COUNT(k) FROM Kudos k WHERE k.recipient.id = ?1 AND TYPE(k.recipient) = Team AND k.isActive = true")
    Long countActiveKudosByTeamRecipientId(Long teamId);

    Page<Kudos> findAll(Pageable pageable);

    interface AdminKudosProjection {
        Long getId();
        String getMessage();
        Boolean getActive();
        Boolean getAnonymous();
        java.time.LocalDateTime getCreatedAt();
        Long getSenderId();
        String getSenderName();
        Long getRecipientId();
        String getRecipientName();
    }

    @Query("SELECT k.id as id, k.message as message, k.isActive as active, k.anonymous as anonymous, k.createdAt as createdAt, " +
           "s.id as senderId, " +
           "s.name as senderName, " +
           "r.id as recipientId, " +
           "CASE TYPE(r) WHEN Employee THEN e.name WHEN Team THEN t.name ELSE null END as recipientName " +
           "FROM Kudos k " +
           "LEFT JOIN k.sender s " +
           "LEFT JOIN k.recipient r " +
           "LEFT JOIN Employee e ON TYPE(r) = Employee AND r.id = e.id " +
           "LEFT JOIN Team t ON TYPE(r) = Team AND r.id = t.id " +
           "WHERE LOWER(k.message) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "   OR (k.anonymous = false AND LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "   OR LOWER(CASE TYPE(r) WHEN Employee THEN e.name WHEN Team THEN t.name ELSE '' END) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<AdminKudosProjection> searchAdminKudos(@org.springframework.data.repository.query.Param("search") String search, Pageable pageable);

    @Query("SELECT COUNT(k) > 0 FROM Kudos k WHERE k.sender.id = :senderId AND k.recipient.id = :recipientId AND FUNCTION('DATE', k.createdAt) = FUNCTION('CURRENT_DATE')")
    boolean existsBySenderIdAndRecipientIdAndCreatedAtToday(@org.springframework.data.repository.query.Param("senderId") Long senderId, @org.springframework.data.repository.query.Param("recipientId") Long recipientId);
} 