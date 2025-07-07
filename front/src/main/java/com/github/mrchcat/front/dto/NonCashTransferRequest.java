package com.github.mrchcat.front.dto;

import com.github.mrchcat.front.model.BankCurrency;
import com.github.mrchcat.front.model.TransferDirection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import org.springframework.web.bind.annotation.PostMapping;

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
