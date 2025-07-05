package com.github.mrchcat.accounts.blocks.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Table("amount_blocks")
@Getter
@Setter
public class AccountBlock {
    @Id
    long id;
    @Column("blocking_transaction_id")
    UUID blockingTransactionId;
    @Column("account_id")
    UUID accountId;
    @Column("amount")
    BigDecimal amount;
    @Column("is_active")
    boolean isActive;
    @Column("created_at")
    LocalDateTime createdAt;
    @Column("updated_at")
    LocalDateTime updatedAt;
}
