package com.github.mrchcat.accounts.account.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BankCurrencyListDto(@NotNull List<String> string_code_iso4217) {
}
