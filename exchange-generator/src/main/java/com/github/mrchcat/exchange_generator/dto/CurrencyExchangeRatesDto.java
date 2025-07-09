package com.github.mrchcat.exchange_generator.dto;

import com.github.mrchcat.exchange_generator.model.BankCurrency;
import com.github.mrchcat.exchange_generator.model.CurrencyRate;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record CurrencyExchangeRatesDto(
        BankCurrency baseCurrency,
        List<CurrencyRate> exchangeRates
) {
}
