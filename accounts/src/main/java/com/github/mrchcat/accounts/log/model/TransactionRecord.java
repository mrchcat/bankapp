package com.github.mrchcat.accounts.log.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("log")
public class TransactionRecord {
    @Id
    UUID id;

    @Column("created_at")
    LocalDateTime createdAt;

    @Column("transaction_type")
    String transactionType;

    @Column("from_account_id")
    UUID fromAccountId;

    @Column("to_account_id")
    UUID toAccountId;

    @Column("amount_from")
    BigDecimal amountFrom;

    @Column("amount_to")
    BigDecimal amountTo;

    @Column("is_succeed")
    boolean isSucceed;
}
