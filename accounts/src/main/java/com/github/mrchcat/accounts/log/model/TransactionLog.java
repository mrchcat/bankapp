package com.github.mrchcat.accounts.log.model;

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
@Getter
@Setter
@Table("log")
public class TransactionLog {
    @Id
    long id;

    @Column("transaction_id")
    UUID transactionId;

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

    @Column("created_at")
    LocalDateTime createdAt;
}

