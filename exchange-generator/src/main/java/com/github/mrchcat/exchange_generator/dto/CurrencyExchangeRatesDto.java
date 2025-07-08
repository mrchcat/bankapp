package com.github.mrchcat.exchange_generator.dto;

import com.github.mrchcat.exchange_generator.model.BankCurrency;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Builder
public record CurrencyExchangeRatesDto(
        BankCurrency baseCurrency,
        Map<BankCurrency, BigDecimal> exchangeRates,
        LocalDateTime time
) {
}
