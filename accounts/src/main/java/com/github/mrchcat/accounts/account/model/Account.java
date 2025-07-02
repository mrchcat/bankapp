package com.github.mrchcat.accounts.account.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
public class Account {
    UUID id;
    String number;
    BigDecimal balance;
    BankCurrency currency;
    UUID userId;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    boolean isActive;
}
