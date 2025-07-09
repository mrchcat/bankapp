package com.github.mrchcat.exchange_generator.dto;

import com.github.mrchcat.exchange_generator.model.BankCurrency;
import com.github.mrchcat.exchange_generator.model.CurrencyRate;

import java.util.List;

public record CurrencyExchangeRatesDto(
        BankCurrency baseCurrency,
        List<CurrencyRate> exchangeRates
) {
}
