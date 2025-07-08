package com.github.mrchcat.exchange_generator.service;

import com.github.mrchcat.exchange_generator.dto.CurrencyExchangeRatesDto;
import com.github.mrchcat.exchange_generator.model.BankCurrency;
import com.github.mrchcat.exchange_generator.security.OAuthHeaderGetter;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GeneratorServiceImpl implements GeneratorService {
    private final String EXCHANGE_SERVICE = "bankExchange";
    private final String EXCHANGE_SEND_RATES = "/exchange";
    private final BigDecimal AVERAGE_USD = BigDecimal.valueOf(80);
    private final BigDecimal AVERAGE_CNY = BigDecimal.valueOf(11);

    private final RestClient.Builder restClientBuilder;
    private final OAuthHeaderGetter oAuthHeaderGetter;

    @Override
    @Scheduled(fixedDelay = 5000L)
    public void sendNewRates() {
        var currencyMap = getRates();
        var rates = CurrencyExchangeRatesDto.builder()
                .baseCurrency(BankCurrency.RUB)
                .exchangeRates(currencyMap)
                .time(LocalDateTime.now())
                .build();
        send(rates);
    }

    private Map<BankCurrency, BigDecimal> getRates() {
        BigDecimal randomUsd = BigDecimal.valueOf(Math.random() + 1);
        BigDecimal randomCNY = BigDecimal.valueOf(Math.random() + 1);
        return Map.of(BankCurrency.USD, AVERAGE_USD.multiply(randomUsd),
                BankCurrency.CNY, AVERAGE_CNY.multiply(randomCNY));
    }

    private void send(CurrencyExchangeRatesDto rates) {
        try {
            System.out.println("отправляем " + rates);
            var oAuthHeader = oAuthHeaderGetter.getOAuthHeader();
            String url = "http://" + EXCHANGE_SERVICE + EXCHANGE_SEND_RATES;
            var response = restClientBuilder.build()
                    .post()
                    .uri(url)
                    .header(oAuthHeader.name(), oAuthHeader.value())
                    .body(rates)
                    .retrieve()
                    .toBodilessEntity();
            System.out.println("получили response=" + response);
        } catch (Exception ignore) {
            System.out.println("ошибка " + ignore.getMessage());
        }
    }

}
