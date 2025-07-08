package com.github.mrchcat.transfer.dto;

import jakarta.validation.constraints.NotNull;

public record BlockerResponseDto(@NotNull Boolean isConfirmed, String reason) {
}
