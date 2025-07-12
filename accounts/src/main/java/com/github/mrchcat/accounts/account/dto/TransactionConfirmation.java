package com.github.mrchcat.accounts.account.dto;

import com.github.mrchcat.shared.enums.TransactionStatus;

import java.util.UUID;

public record TransactionConfirmation(UUID transactionId, TransactionStatus status) {
}
