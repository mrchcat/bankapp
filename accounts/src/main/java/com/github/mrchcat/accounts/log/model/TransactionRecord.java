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
    LocalDateTime created_at;

    @Column("transaction_type")
    String transactionType;

    @Column("from_account_id")
    UUID fromAccountId;

    @Column("to_account_id")
    UUID toAccountId;

    @Column("amount_from")
    BigDecimal amount_from;

    @Column("amount_to")
    BigDecimal amount_to;

    @Column("is_succeed")
    boolean is_succeed;
}
