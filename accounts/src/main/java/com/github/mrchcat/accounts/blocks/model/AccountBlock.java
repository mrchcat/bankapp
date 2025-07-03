package com.github.mrchcat.accounts.blocks.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("account_blocks")
public class AccountBlock {
    @Id
    long id;
    @Column("blocking_transaction_id")
    UUID blockingTransactionId;
    @Column("account_id")
    UUID accountId;
    @Column("amount")
    BigDecimal amount;
    @Column("isActive")
    boolean isActive;
    @Column("created_at")
    LocalDateTime createdAt;
    @Column("updated_at")
    LocalDateTime updatedAt;

}
