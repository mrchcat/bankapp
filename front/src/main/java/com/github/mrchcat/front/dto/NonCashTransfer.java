package com.github.mrchcat.front.dto;

import com.github.mrchcat.front.model.BankCurrency;
import com.github.mrchcat.front.model.TransferDirection;
import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record NonCashTransfer(
        @NotNull(message = "ошибка: не указан тип перевода")
        TransferDirection direction,

        @NotNull(message = "ошибка: не указана валюта")
        BankCurrency fromCurrency,

        BankCurrency toCurrency,

        @NotNull(message = "ошибка: сумма должна быть положительным числом")
        @Positive(message = "ошибка: сумма должна быть положительным числом")
        BigDecimal amount,

        @NotNull(message = "пользователь не может быть пуст")
        @NotBlank(message = "пользователь не может быть пуст")
        String fromUsername,

        String toUsername

) {
    @AssertFalse(message = "ошибка: счета совпадают")
    boolean isSameAccountsForTransferToYourself() {
        if (direction.equals(TransferDirection.YOURSELF)) {
            return fromCurrency.equals(toCurrency);
        }
        if (direction.equals(TransferDirection.OTHER) && fromUsername.equals(toUsername)) {
            return fromCurrency.equals(toCurrency);
        }

        return false;
    }

    @AssertFalse(message = "ошибка: валюта счета назначения не может быть пуста")
    boolean isEmptyToCurrency() {
        if (direction.equals(TransferDirection.OTHER)) {
            return toCurrency == null;
        }
        return false;
    }

    @AssertFalse(message = "ошибка: получатель не может быть пуст")
    boolean isEmptyToUsername() {
        if (direction.equals(TransferDirection.OTHER)) {
            return toUsername == null || toUsername.isBlank();
        }
        return false;
    }
}
