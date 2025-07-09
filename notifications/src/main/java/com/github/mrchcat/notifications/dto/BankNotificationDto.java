package com.github.mrchcat.notifications.dto;

import org.springframework.data.relational.core.mapping.Column;

public record BankNotificationDto(
        String service,
        String username,
        String fullName,
        String email,
        String message) {
}
