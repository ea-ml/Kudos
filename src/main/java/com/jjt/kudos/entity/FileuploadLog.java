package com.jjt.kudos.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileuploadLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long uploaderId;

    private String status;

    private LocalDateTime timestamp;

    @Column(nullable = true)
    private String errorMessage;

    private int createdCount;
} 