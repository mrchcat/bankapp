package com.github.mrchcat.transfer.dto;


import com.github.mrchcat.transfer.model.BankCurrency;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CurrencyExchangeRateDto(
        @NotNull
        BankCurrency from,
        @NotNull
        BankCurrency to,
        @NotNull
        BigDecimal rate
) {
}
