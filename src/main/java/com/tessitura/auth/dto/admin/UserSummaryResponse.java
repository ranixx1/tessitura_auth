package com.tessitura.auth.dto.admin;

import java.time.LocalDateTime;

public record UserSummaryResponse(
        Long id,
        String username,
        String email,
        String role,
        boolean active,
        LocalDateTime lastLogin,
        LocalDateTime createdAt
) {}