package com.github.mrchcat.shared.cash;

import com.github.mrchcat.shared.enums.BankCurrency;
import com.github.mrchcat.shared.enums.CashAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CashTransactionDto(

        @NotNull(message = "ошибка: не указано имя")
        @NotBlank(message = "ошибка: не указано имя")
        String username,

        @NotNull(message = "ошибка: сумма не может быть пустой")
        @Positive(message = "ошибка: сумма должна быть положительным числом")
        BigDecimal value,

        @NotNull(message = "ошибка: не указана валюта")
        BankCurrency currency,

        @NotNull(message = "ошибка: тип операции не может быть пуст")
        CashAction action
) {
}

