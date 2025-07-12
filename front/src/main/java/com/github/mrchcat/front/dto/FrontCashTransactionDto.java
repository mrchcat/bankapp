package com.github.mrchcat.front.dto;

import com.github.mrchcat.shared.enums.BankCurrency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record FrontCashTransactionDto(
        @NotNull(message = "ошибка: не указана валюта")
        BankCurrency accountCurrency,

        @Positive(message = "ошибка: сумма должна быть положительным числом")
        BigDecimal value
) {
}
