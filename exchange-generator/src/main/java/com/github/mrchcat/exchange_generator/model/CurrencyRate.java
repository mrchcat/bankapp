package com.github.mrchcat.exchange_generator.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record CurrencyRate(BankCurrency currency, BigDecimal buyRate, BigDecimal sellRate, LocalDateTime time) {
}
