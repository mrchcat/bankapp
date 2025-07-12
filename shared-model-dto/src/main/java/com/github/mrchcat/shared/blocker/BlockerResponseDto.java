package com.github.mrchcat.shared.blocker;

import jakarta.validation.constraints.NotNull;

public record BlockerResponseDto(@NotNull Boolean isConfirmed, String reason) {
}
