package com.github.mrchcat.exchange.dto;

import com.github.mrchcat.exchange.model.BankCurrency;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public record CurrencyExchangeRatesDto(
        @NotNull
        BankCurrency baseCurrency,
        @NotNull
        Map<BankCurrency, BigDecimal> exchangeRates,
        @NotNull
        LocalDateTime time
) {
}
