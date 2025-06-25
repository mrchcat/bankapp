package com.github.mrchcat.accounts.user.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record BankNotificationDto(UUID id, String fullName, String email, String message) {
}
