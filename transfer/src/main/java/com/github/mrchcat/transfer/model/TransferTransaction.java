package com.github.mrchcat.transfer.model;

import com.github.mrchcat.shared.enums.TransactionStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
public class TransferTransaction {
    long id;
    UUID transactionId;
    UUID fromAccount;
    UUID toAccount;
    BigDecimal fromAmount;
    BigDecimal toAmount;
    BigDecimal exchangeRate;
    TransactionStatus status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}