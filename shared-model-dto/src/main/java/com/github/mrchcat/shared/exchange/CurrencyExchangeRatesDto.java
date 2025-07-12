package com.github.mrchcat.shared.exchange;


import com.github.mrchcat.shared.enums.BankCurrency;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CurrencyExchangeRatesDto(
        @NotNull
        BankCurrency baseCurrency,
        @NotNull
        List<CurrencyRate> exchangeRates
) {
}
