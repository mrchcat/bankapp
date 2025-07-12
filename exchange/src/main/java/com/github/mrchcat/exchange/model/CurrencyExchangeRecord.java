package com.github.mrchcat.exchange.model;

import com.github.mrchcat.shared.enums.BankCurrency;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class CurrencyExchangeRecord {
    long id;
    BankCurrency baseCurrency;
    BankCurrency exchangeCurrency;
    BigDecimal buyRate;
    BigDecimal sellRate;
    LocalDateTime time;
    LocalDateTime createdAt;
}
