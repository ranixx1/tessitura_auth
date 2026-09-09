package com.tessitura.auth.dto.admin;

import java.time.LocalDateTime;

public record AuditSummary(
        LocalDateTime loginTime,
        String ipAddress,
        boolean success,
        String failureReason,
        String userAgent
) {}