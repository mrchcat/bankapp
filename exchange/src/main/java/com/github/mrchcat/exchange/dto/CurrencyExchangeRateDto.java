package com.github.mrchcat.exchange.dto;

import com.github.mrchcat.exchange.model.BankCurrency;
import lombok.Builder;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Setter
public class CurrencyExchangeRateDto {
    BankCurrency baseCurrency;
    BankCurrency exchangeCurrency;
    BigDecimal rate;
    LocalDateTime time;
}
