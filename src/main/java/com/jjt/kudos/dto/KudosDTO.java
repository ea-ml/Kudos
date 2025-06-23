package com.jjt.kudos.dto;

import com.jjt.kudos.entity.Kudos;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KudosDTO {
    private Long id;
    private Long senderId;
    private Long recipientId;
    private String message;
    private boolean isActive;
    private boolean anonymous;
    private LocalDateTime createdAt;
    private String senderName;
    private String recipientName;

    public KudosDTO(Kudos k) {
        this.id = k.getId();
        this.senderId = k.getSender() != null ? k.getSender().getId() : null;
        this.recipientId = k.getRecipient() != null ? k.getRecipient().getId() : null;
        this.message = k.getMessage();
        this.isActive = k.isActive();
        this.anonymous = k.isAnonymous();
        this.createdAt = k.getCreatedAt();
        this.senderName = k.isAnonymous() ? "Anonymous" : (k.getSender() != null ? k.getSender().getName() : "");
        if (k.getRecipient() instanceof com.jjt.kudos.entity.Employee emp) {
            this.recipientName = emp.getName();
        } else if (k.getRecipient() instanceof com.jjt.kudos.entity.Team team) {
            this.recipientName = team.getName();
        } else {
            this.recipientName = "";
        }
    }

    @Override
    public String toString() {
        return "KudosDTO{" +
                "id=" + id +
                ", senderId=" + senderId +
                ", recipientId=" + recipientId +
                ", message='" + message + '\'' +
                ", isActive=" + isActive +
                ", isAnonymous=" + anonymous +
                ", createdAt=" + createdAt +
                ", senderName='" + senderName + '\'' +
                ", recipientName='" + recipientName + '\'' +
                '}';
    }
} 