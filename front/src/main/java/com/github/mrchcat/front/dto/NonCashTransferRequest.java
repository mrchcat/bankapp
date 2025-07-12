package com.github.mrchcat.front.dto;

import com.github.mrchcat.shared.enums.BankCurrency;
import com.github.mrchcat.shared.enums.TransferDirection;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record NonCashTransferRequest(
        TransferDirection direction,
        BankCurrency fromCurrency,
        BankCurrency toCurrency,
        BigDecimal amount,
        String fromUsername,
        String toUsername) {
}
