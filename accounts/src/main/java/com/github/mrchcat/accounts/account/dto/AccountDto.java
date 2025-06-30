package com.github.mrchcat.accounts.account.dto;

import com.github.mrchcat.accounts.account.model.BankCurrency;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record AccountDto(UUID id,
                         String number,
                         BigDecimal balance,
                         String currencyStringCode,
                         String currencyRuName) {
}
