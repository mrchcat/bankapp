package com.github.mrchcat.cash.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@ToString
public class CashTransaction {
    long id;
    UUID transactionId;
    CashAction action;
    UUID userId;
    String username;
    UUID accountId;
    BankCurrency currencyStringCodeIso4217;
    BigDecimal amount;
    TransactionStatus status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
