package com.github.mrchcat.cash.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BlockSumDto(UUID id, UUID accountId, BigDecimal amount) {
}
