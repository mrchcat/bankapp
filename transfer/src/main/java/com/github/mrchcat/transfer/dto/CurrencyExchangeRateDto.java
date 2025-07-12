package com.github.mrchcat.transfer.dto;



import com.github.mrchcat.shared.enums.BankCurrency;
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
