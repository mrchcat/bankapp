package com.github.mrchcat.cash.dto;

import com.github.mrchcat.shared.enums.CashAction;
import com.github.mrchcat.shared.enums.TransactionStatus;
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
