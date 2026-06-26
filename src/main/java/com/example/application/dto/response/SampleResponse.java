package com.example.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record SampleResponse(UUID id, String name, LocalDateTime createdAt) {}
