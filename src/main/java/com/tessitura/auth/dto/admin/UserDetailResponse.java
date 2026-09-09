package com.tessitura.auth.dto.admin;

import java.time.LocalDateTime;
import java.util.List;

public record UserDetailResponse(
        Long id,
        String username,
        String name,
        String email,
        String phoneNumber,
        String role,
        boolean active,
        int failedAttempts,
        LocalDateTime createdAt,
        LocalDateTime lastLogin,
        List<AuditSummary> recentActivity
) {}