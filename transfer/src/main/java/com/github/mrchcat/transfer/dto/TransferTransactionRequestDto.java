package com.github.mrchcat.transfer.dto;

import com.github.mrchcat.transfer.model.TransactionStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record TransferTransactionRequestDto(UUID transactionId,
                                            UUID fromAccount,
                                            UUID toAccount,
                                            BigDecimal fromAmount,
                                            BigDecimal toAmount,
                                            TransactionStatus status) {
}
