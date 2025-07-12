package com.github.mrchcat.exchange.service;

import com.github.mrchcat.exchange.dto.CurrencyExchangeRateDto;
import com.github.mrchcat.exchange.dto.CurrencyExchangeRatesDto;
import com.github.mrchcat.exchange.dto.CurrencyRate;
import com.github.mrchcat.exchange.exceptions.ExchangeGeneratorServiceException;
import com.github.mrchcat.exchange.model.CurrencyExchangeRecord;
import com.github.mrchcat.exchange.repository.ExchangeRepository;
import com.github.mrchcat.shared.enums.BankCurrency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {
    private final ExchangeRepository exchangeRepository;

    private static final ConcurrentHashMap<BankCurrency, CurrencyRate> exchangeRates = new ConcurrentHashMap<>();
    private static final BankCurrency baseCurrencyByDefault = BankCurrency.RUB;

    static {
        exchangeRates.put(baseCurrencyByDefault,
                new CurrencyRate(baseCurrencyByDefault, BigDecimal.ONE, BigDecimal.ONE, null));
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
            dto.setRate(exchangeRates.get(fromCurrency).sellRate());
            return dto;
        }
//        если обе валюты не основные
        if (!exchangeRates.containsKey(toCurrency)) {
            throw new NoSuchElementException(toCurrency.name());
        }
        BigDecimal fromCurrencyInDefault = exchangeRates.get(fromCurrency).sellRate();
        BigDecimal toCurrencyInDefault = exchangeRates.get(toCurrency).buyRate();
        BigDecimal rate = fromCurrencyInDefault.divide(toCurrencyInDefault, 5, RoundingMode.CEILING);
        dto.setRate(rate);
        return dto;
    }

    @Override
    public Collection<CurrencyRate> getAllRates() {
        return exchangeRates.values();
    }

    @Override
    public void saveRates(CurrencyExchangeRatesDto rates) {
        if (!rates.baseCurrency().equals(baseCurrencyByDefault)) {
            throw new ExchangeGeneratorServiceException("");
        }
        for (CurrencyRate currencyRate : rates.exchangeRates()) {
            BankCurrency currency = currencyRate.currency();
            if (!currency.equals(baseCurrencyByDefault)) {
                exchangeRates.put(currency, currencyRate);
                var record = CurrencyExchangeRecord.builder()
                        .baseCurrency(baseCurrencyByDefault)
                        .exchangeCurrency(currency)
                        .buyRate(currencyRate.buyRate())
                        .sellRate(currencyRate.sellRate())
                        .time(currencyRate.time())
                        .build();
                exchangeRepository.save(record);
            }
        }
    }
}
