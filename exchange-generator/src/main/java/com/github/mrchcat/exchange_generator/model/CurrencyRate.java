package com.github.mrchcat.exchange_generator.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

@Builder
public record CurrencyRate(BankCurrency currency, BigDecimal buyRate, BigDecimal sellRate, LocalDateTime time) {
}
