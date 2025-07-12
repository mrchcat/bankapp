package com.github.mrchcat.shared.accounts;

import com.github.mrchcat.shared.enums.TransactionStatus;
import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record AccountTransferTransactionDto(
        @NotNull(message = "проблема transactionId")
        UUID transactionId,
        @NotNull(message = "проблема fromAccount")
        UUID fromAccount,
        @NotNull
        UUID toAccount,
        @NotNull
        BigDecimal fromAmount,
        @NotNull
        BigDecimal toAmount,
        @NotNull
        TransactionStatus status) {

    @AssertFalse
    boolean isAccountsEqual() {
        return fromAccount.equals(toAccount);
    }
}
