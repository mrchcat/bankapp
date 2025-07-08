package com.github.mrchcat.profile.dto;


import com.github.mrchcat.profile.model.BankCurrency;
import com.github.mrchcat.profile.model.TransferDirection;
import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record NonCashTransferDto(
        @NotNull
        TransferDirection direction,
        @NotNull
        BankCurrency fromCurrency,
        BankCurrency toCurrency,
        @NotNull
        BigDecimal amount,
        @NotNull
        @NotBlank
        String fromUsername,
        String toUsername) {

    @AssertFalse(message = "ошибка: имя получателя не может быть пусто")
    boolean isUsernameEmpty() {
        if (direction.equals(TransferDirection.OTHER)) {
            return toUsername == null || toUsername.isBlank();
        }
        return false;
    }

    @AssertFalse(message = "ошибка: валюта получения и отправления не могут совпадать при отправке самому себе")
    boolean isSameCurrencies() {
        if (direction.equals(TransferDirection.YOURSELF)) {
            return fromCurrency == toCurrency;
        } else if (fromUsername.equals(toUsername)) {
            return fromCurrency == toCurrency;
        }
        return false;
    }
}
