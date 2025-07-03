package com.github.mrchcat.cash.dto;

import jakarta.validation.constraints.NotNull;

public record BlockerResponseDto(@NotNull Boolean isConfirmed, String reason) {
}
