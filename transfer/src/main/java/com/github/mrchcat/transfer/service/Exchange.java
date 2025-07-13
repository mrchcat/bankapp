package com.github.mrchcat.transfer.service;

import com.github.mrchcat.shared.enums.BankCurrency;
import com.github.mrchcat.shared.exchange.CurrencyExchangeRateDto;
import com.github.mrchcat.transfer.exception.ExchangeServiceException;
import com.github.mrchcat.transfer.security.OAuthHeaderGetter;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class Exchange {
    private final String EXCHANGE_SERVICE = "bankExchange";
    private final String EXCHANGE_GET_EXCHANGE_RATE = "/exchange";
    private final RestClient.Builder restClientBuilder;
    private final OAuthHeaderGetter oAuthHeaderGetter;

    @CircuitBreaker(name = "exchange")
    @Retry(name = "exchange")
    public BigDecimal getExchangeRate(BankCurrency fromCurrency, BankCurrency toCurrency) throws AuthException {
        if (fromCurrency.equals(toCurrency)) {
            return BigDecimal.ONE;
        }
        var oAuthHeader = oAuthHeaderGetter.getOAuthHeader();
        String requestUrl = "http://"
                + EXCHANGE_SERVICE + EXCHANGE_GET_EXCHANGE_RATE + "/" + fromCurrency.name() + "?toCurrency=" + toCurrency.name();
        try {
            var exchangeRate = restClientBuilder.build()
                    .get()
                    .uri(requestUrl)
                    .header(oAuthHeader.name(), oAuthHeader.value())
                    .retrieve()
                    .body(CurrencyExchangeRateDto.class);
            if (exchangeRate == null) {
                throw new ExchangeServiceException("");
            }
            return exchangeRate.getRate();
        } catch (Exception ex) {
            throw new ExchangeServiceException("");
        }
    }

}
