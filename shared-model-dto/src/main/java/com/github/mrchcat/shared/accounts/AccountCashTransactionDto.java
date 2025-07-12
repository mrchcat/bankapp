package com.github.mrchcat.shared.accounts;

import com.github.mrchcat.shared.enums.CashAction;
import com.github.mrchcat.shared.enums.TransactionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record AccountCashTransactionDto(
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
