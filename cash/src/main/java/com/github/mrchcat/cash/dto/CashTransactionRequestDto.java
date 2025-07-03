package com.github.mrchcat.cash.dto;

import com.github.mrchcat.cash.model.CashAction;
import com.github.mrchcat.cash.model.TransactionStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record CashTransactionRequestDto(UUID transactionId,
                                        UUID accountId,
                                        BigDecimal amount,
                                        CashAction action,
                                        TransactionStatus status) {
}
