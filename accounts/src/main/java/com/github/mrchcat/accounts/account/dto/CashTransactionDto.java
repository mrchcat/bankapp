package com.github.mrchcat.accounts.account.dto;

import com.github.mrchcat.accounts.account.model.CashAction;
import com.github.mrchcat.accounts.account.model.TransactionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record CashTransactionDto(
        @NotNull
        UUID transactionId,
        @NotNull
        UUID account,
        @NotNull
        BigDecimal amount,
        @NotNull
        CashAction action,
        @NotNull
        TransactionStatus status) {
}
