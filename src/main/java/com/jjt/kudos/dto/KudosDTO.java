package com.jjt.kudos.dto;

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
                '}';
    }
} 