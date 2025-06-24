package com.github.mrchcat.notifications.domain;

import lombok.Builder;

import java.util.UUID;

@Builder
public record BankNotification(UUID id, String fullName, String email, String message) {
}
