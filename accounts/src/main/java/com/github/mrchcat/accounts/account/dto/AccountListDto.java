package com.github.mrchcat.accounts.account.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record AccountListDto(@NotNull List<UUID> accountsId) {
}
