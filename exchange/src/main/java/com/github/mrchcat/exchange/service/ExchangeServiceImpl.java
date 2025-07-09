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
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {
    private static final ConcurrentHashMap<BankCurrency, BigDecimal> exchangeRates = new ConcurrentHashMap<>();
    private static final BankCurrency baseCurrencyByDefault = BankCurrency.RUB;
    private final ExchangeRepository exchangeRepository;

    static {
        exchangeRates.put(baseCurrencyByDefault, BigDecimal.ONE);
    }

    @Override
    public CurrencyExchangeRateDto getExchangeRate(BankCurrency fromCurrency, BankCurrency toCurrency) {
        var dto = CurrencyExchangeRateDto.builder()
                .from(fromCurrency)
                .to(toCurrency)
                .build();
        //если валюты совпадают
        if (fromCurrency.equals(toCurrency)) {
            dto.setRate(BigDecimal.ONE);
            return dto;
        }
        //если меняем на основную валюту
        if (!exchangeRates.containsKey(fromCurrency)) {
            throw new NoSuchElementException(fromCurrency.name());
        }
        if (toCurrency.equals(baseCurrencyByDefault)) {
            dto.setRate(exchangeRates.get(fromCurrency));
            return dto;
        }
//        если обе валюты не основные
        if (!exchangeRates.containsKey(toCurrency)) {
            throw new NoSuchElementException(toCurrency.name());
        }
        BigDecimal fromCurrencyInDefault=exchangeRates.get(fromCurrency);
        BigDecimal toCurrencyInDefault=exchangeRates.get(toCurrency);
        BigDecimal rate=fromCurrencyInDefault.divide(toCurrencyInDefault, 5, RoundingMode.CEILING);
        dto.setRate(rate);
        return dto;
    }

    @Override
    public Map<BankCurrency, BigDecimal> getAllRates() {
        return exchangeRates;
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
