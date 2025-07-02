package com.github.mrchcat.cash.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record AccountDto(UUID id,
                         String number,
                         BigDecimal balance,
                         String currencyStringCode) {
}
