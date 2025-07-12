package com.github.mrchcat.notifications.dto;

import lombok.Builder;

@Builder
public record BankNotificationDto(
        String service,
        String username,
        String fullName,
        String email,
        String message) {
}
