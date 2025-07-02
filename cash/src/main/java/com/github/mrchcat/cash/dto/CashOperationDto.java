package com.github.mrchcat.cash.dto;

import com.github.mrchcat.cash.model.BankCurrency;
import com.github.mrchcat.cash.model.CashOperation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CashOperationDto(

        @NotNull(message = "ошибка: не указана валюта")
        @NotBlank(message = "ошибка: не указана валюта")
        String username,

        @NotNull(message = "ошибка: сумма не может быть пустой")
        @Positive(message = "ошибка: сумма должна быть положительным числом")
        BigDecimal value,

        @NotNull(message = "ошибка: не указана валюта")
        BankCurrency currency,

        @NotNull(message = "ошибка: валюта не может быть пустой")
        CashOperation operation
) {
}

