package com.github.mrchcat.exchange.controller;

import com.github.mrchcat.exchange.service.ExchangeService;
import com.github.mrchcat.shared.enums.BankCurrency;
import com.github.mrchcat.shared.exchange.CurrencyExchangeRateDto;
import com.github.mrchcat.shared.exchange.CurrencyExchangeRatesDto;
import com.github.mrchcat.shared.exchange.CurrencyRate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
public class ExchangeController {
    private final ExchangeService exchangeService;

    @GetMapping("/exchange/{fromCurrency}")
    CurrencyExchangeRateDto getExchangeRate(@PathVariable("fromCurrency") BankCurrency fromCurrency,
                                            @RequestParam("toCurrency") BankCurrency toCurrency) {
        return exchangeService.getExchangeRate(fromCurrency, toCurrency);
    }

    @GetMapping("/exchange")
    Collection<CurrencyRate> getAllRates() {
        return exchangeService.getAllRates();
    }

    @PostMapping("/exchange")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void getAllRates(@RequestBody @Valid CurrencyExchangeRatesDto rates) {
        exchangeService.saveRates(rates);
    }
}
