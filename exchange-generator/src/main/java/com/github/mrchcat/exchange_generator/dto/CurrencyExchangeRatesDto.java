package com.github.mrchcat.exchange_generator.dto;

import com.github.mrchcat.exchange_generator.model.CurrencyRate;
import com.github.mrchcat.shared.enums.BankCurrency;

import java.util.List;

public record CurrencyExchangeRatesDto(
        BankCurrency baseCurrency,
        List<CurrencyRate> exchangeRates
) {
}
