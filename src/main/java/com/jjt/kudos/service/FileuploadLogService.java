package com.jjt.kudos.service;

import com.jjt.kudos.entity.FileuploadLog;
import com.jjt.kudos.repository.FileuploadLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FileuploadLogService {
    private final FileuploadLogRepository fileuploadLogRepository;

    public void logUpload(Long uploaderId, String status, String errorMessage, int createdCount) {
        FileuploadLog log = new FileuploadLog();
        log.setUploaderId(uploaderId);
        log.setStatus(status);
        log.setTimestamp(LocalDateTime.now());
        log.setErrorMessage(errorMessage);
        log.setCreatedCount(createdCount);
        fileuploadLogRepository.save(log);
    }
} 