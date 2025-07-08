package com.github.mrchcat.transfer.dto;


import com.github.mrchcat.transfer.model.BankCurrency;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record CurrencyExchangeRateDto(
        BankCurrency baseCurrency,
        BankCurrency exchangeCurrency,
        BigDecimal amount,
        LocalDateTime time
) {
}
