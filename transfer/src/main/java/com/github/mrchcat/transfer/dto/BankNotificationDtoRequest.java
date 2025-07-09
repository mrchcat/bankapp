package com.github.mrchcat.transfer.dto;

import lombok.Builder;

@Builder
public record BankNotificationDtoRequest(
        String service,
        String username,
        String fullName,
        String email,
        String message) {
}
