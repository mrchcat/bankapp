package com.github.mrchcat.exchange.service;

import com.github.mrchcat.shared.enums.BankCurrency;
import com.github.mrchcat.shared.exchange.CurrencyExchangeRateDto;
import com.github.mrchcat.shared.exchange.CurrencyExchangeRatesDto;
import com.github.mrchcat.shared.exchange.CurrencyRate;

import java.util.Collection;

public interface ExchangeService {

    CurrencyExchangeRateDto getExchangeRate(BankCurrency baseCurrency, BankCurrency exchangeCurrency);

    Collection<CurrencyRate> getAllRates();

    void saveRates(CurrencyExchangeRatesDto rates);
}
