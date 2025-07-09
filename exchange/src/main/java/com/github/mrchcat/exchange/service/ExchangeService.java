package com.github.mrchcat.exchange.service;

import com.github.mrchcat.exchange.dto.CurrencyExchangeRateDto;
import com.github.mrchcat.exchange.dto.CurrencyExchangeRatesDto;
import com.github.mrchcat.exchange.dto.CurrencyRate;
import com.github.mrchcat.exchange.model.BankCurrency;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ExchangeService {

    CurrencyExchangeRateDto getExchangeRate(BankCurrency baseCurrency, BankCurrency exchangeCurrency);

    Collection<CurrencyRate> getAllRates();

    void saveRates(CurrencyExchangeRatesDto rates);
}
