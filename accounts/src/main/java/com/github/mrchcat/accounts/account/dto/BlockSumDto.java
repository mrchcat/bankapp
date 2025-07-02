package com.github.mrchcat.accounts.account.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record BlockSumDto(@NotNull UUID id,
                          @NotNull UUID accountId,
                          @NotNull @Positive BigDecimal amount) {
}
