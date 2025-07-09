package com.github.mrchcat.exchange.controller;

import com.github.mrchcat.exchange.dto.CurrencyExchangeRateDto;
import com.github.mrchcat.exchange.dto.CurrencyExchangeRatesDto;
import com.github.mrchcat.exchange.dto.CurrencyRate;
import com.github.mrchcat.exchange.model.BankCurrency;
import com.github.mrchcat.exchange.service.ExchangeService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ExchangeController {
    private final ExchangeService exchangeService;

    @GetMapping("/exchange/{fromCurrency}")
    CurrencyExchangeRateDto getExchangeRate(@PathVariable("fromCurrency") BankCurrency fromCurrency,
                                            @RequestParam("toCurrency") BankCurrency toCurrency) {
        System.out.println("получили " + fromCurrency + "  " + toCurrency);
        return exchangeService.getExchangeRate(fromCurrency, toCurrency);
    }

    @GetMapping("/exchange")
    Collection<CurrencyRate> getAllRates() {
        return exchangeService.getAllRates();
    }

    @PostMapping("/exchange")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void getAllRates(@RequestBody @Valid CurrencyExchangeRatesDto rates) {
        System.out.println("получили " + rates);
        exchangeService.saveRates(rates);
    }
}
