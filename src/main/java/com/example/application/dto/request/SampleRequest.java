package com.example.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SampleRequest(@NotBlank String name) {}
