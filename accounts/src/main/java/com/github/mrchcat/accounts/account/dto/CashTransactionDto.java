package com.github.mrchcat.accounts.account.dto;

import com.github.mrchcat.shared.enums.CashAction;
import com.github.mrchcat.shared.enums.TransactionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record CashTransactionDto(
        @NotNull
        UUID transactionId,
        @NotNull
        UUID accountId,
        @NotNull
        BigDecimal amount,
        @NotNull
        CashAction action,
        @NotNull
        TransactionStatus status) {
}
