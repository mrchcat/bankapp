package com.github.mrchcat.exchange.service;

import com.github.mrchcat.exchange.dto.CurrencyExchangeRateDto;
import com.github.mrchcat.exchange.dto.CurrencyExchangeRatesDto;
import com.github.mrchcat.exchange.exceptions.ExchangeGeneratorServiceException;
import com.github.mrchcat.exchange.model.BankCurrency;
import com.github.mrchcat.exchange.model.CurrencyExchangeRecord;
import com.github.mrchcat.exchange.repository.ExchangeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {
    private final ConcurrentHashMap<BankCurrency, BigDecimal> exchangeRates = new ConcurrentHashMap<>();
    private final BankCurrency baseCurrencyByDefault = BankCurrency.RUB;
    private final ExchangeRepository exchangeRepository;

    @Override
    public CurrencyExchangeRateDto getExchangeRate(BankCurrency baseCurrency, BankCurrency exchangeCurrency) {
        var dto = CurrencyExchangeRateDto.builder()
                .baseCurrency(baseCurrency)
                .exchangeCurrency(exchangeCurrency)
                .build();
        if (baseCurrency.equals(exchangeCurrency)) {
            dto.setRate(BigDecimal.ONE);
            return dto;
        }
        if (!exchangeRates.containsKey(exchangeCurrency)) {
            throw new NoSuchElementException(exchangeCurrency.name());
        }
        BigDecimal exchangeCurrencyRateToDefault = exchangeRates.get(exchangeCurrency);
        if (baseCurrency.equals(baseCurrencyByDefault)) {
            dto.setRate(exchangeCurrencyRateToDefault);
            return dto;
        }
        if (!exchangeRates.containsKey(baseCurrency)) {
            throw new NoSuchElementException(baseCurrency.name());
        }
        dto.setRate(exchangeRates.get(baseCurrency).divide(exchangeCurrencyRateToDefault, RoundingMode.CEILING));
        return dto;
    }

    @Override
    public void saveRates(CurrencyExchangeRatesDto rates) {
        if (!rates.baseCurrency().equals(baseCurrencyByDefault)) {
            throw new ExchangeGeneratorServiceException("");
        }
        var rateMap = rates.exchangeRates();
        for (BankCurrency currency : BankCurrency.values()) {
            if (!currency.equals(baseCurrencyByDefault) && rateMap.containsKey(currency)) {
                BigDecimal rate = rateMap.get(currency);
                exchangeRates.put(currency, rate);
                var record = CurrencyExchangeRecord.builder()
                        .baseCurrency(baseCurrencyByDefault)
                        .exchangeCurrency(currency)
                        .rate(rate)
                        .time(rates.time())
                        .build();
                exchangeRepository.save(record);
            }
        }
    }
}
