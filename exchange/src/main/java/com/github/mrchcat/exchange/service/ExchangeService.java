package com.github.mrchcat.exchange.service;

import com.github.mrchcat.exchange.dto.CurrencyExchangeRateDto;
import com.github.mrchcat.exchange.dto.CurrencyExchangeRatesDto;
import com.github.mrchcat.exchange.dto.CurrencyRate;
import com.github.mrchcat.exchange.model.BankCurrency;

import java.util.Collection;

public interface ExchangeService {

    CurrencyExchangeRateDto getExchangeRate(BankCurrency baseCurrency, BankCurrency exchangeCurrency);

    Collection<CurrencyRate> getAllRates();

    void saveRates(CurrencyExchangeRatesDto rates);
}
