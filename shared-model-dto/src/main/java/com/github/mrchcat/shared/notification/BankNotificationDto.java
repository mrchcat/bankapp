package com.github.mrchcat.shared.notification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BankNotificationDto(
        @NotNull
        @NotBlank
        String service,
        @NotNull
        @NotBlank
        String username,
        @NotNull
        @NotBlank
        String fullName,
        @NotNull
        @NotBlank
        String email,
        @NotNull
        @NotBlank
        String message) {
}
