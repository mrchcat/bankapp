package com.github.mrchcat.notifications.dto;

public record BankNotificationDto(
        String service,
        String username,
        String fullName,
        String email,
        String message) {
}
