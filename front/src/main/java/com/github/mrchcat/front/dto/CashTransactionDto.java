package com.github.mrchcat.front.dto;

import com.github.mrchcat.front.model.BankCurrency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CashTransactionDto(
        @NotNull(message = "ошибка: не указана валюта")
        BankCurrency accountCurrency,

        @Positive(message = "ошибка: сумма должна быть положительным числом")
        BigDecimal value
) {
}
