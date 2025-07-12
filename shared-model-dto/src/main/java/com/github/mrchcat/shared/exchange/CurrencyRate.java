package com.github.mrchcat.shared.exchange;

import com.github.mrchcat.shared.enums.BankCurrency;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record CurrencyRate(BankCurrency currency, BigDecimal buyRate, BigDecimal sellRate, LocalDateTime time) {
}
