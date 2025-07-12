package com.github.mrchcat.cash.model;

import com.github.mrchcat.shared.enums.BankCurrency;
import com.github.mrchcat.shared.enums.CashAction;
import com.github.mrchcat.shared.enums.TransactionStatus;
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
