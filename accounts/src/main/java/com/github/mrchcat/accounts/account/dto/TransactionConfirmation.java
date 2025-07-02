package com.github.mrchcat.accounts.account.dto;

import com.github.mrchcat.accounts.account.model.TransactionStatus;

import java.util.UUID;

public record TransactionConfirmation(UUID transactionId, TransactionStatus status) {
}
