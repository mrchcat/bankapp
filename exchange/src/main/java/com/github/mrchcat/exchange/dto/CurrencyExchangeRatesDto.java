package com.github.mrchcat.exchange.dto;


import com.github.mrchcat.exchange.model.BankCurrency;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CurrencyExchangeRatesDto(
        @NotNull
        BankCurrency baseCurrency,
        @NotNull
        List<CurrencyRate> exchangeRates
) {
}
