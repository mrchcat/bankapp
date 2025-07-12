package com.github.mrchcat.cash.dto;

import com.github.mrchcat.shared.enums.TransactionStatus;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TransactionConfirmation(@NotNull UUID transactionId, @NotNull TransactionStatus status) {
}
