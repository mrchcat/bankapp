package com.github.mrchcat.accounts.log.model;

import com.github.mrchcat.accounts.account.model.CashAction;
import com.github.mrchcat.accounts.account.model.TransactionStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
public class TransactionLogRecord {
    long id;
    UUID transactionId;
    CashAction action;
    TransactionStatus status;
    UUID fromAccountId;
    UUID toAccountId;
    BigDecimal amountFrom;
    BigDecimal amountTo;
    LocalDateTime createdAt;
}

