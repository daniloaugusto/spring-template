package com.example.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String username,
    String role,
    LocalDateTime createdAt
) {}
