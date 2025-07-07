package com.github.mrchcat.accounts.account.dto;

import com.github.mrchcat.accounts.account.model.TransactionStatus;
import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferTransactionDto(
        @NotNull
        UUID transactionId,
        @NotNull
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
        boolean isAccountsEqual(){
                return fromAccount.equals(toAccount);
        }
}
