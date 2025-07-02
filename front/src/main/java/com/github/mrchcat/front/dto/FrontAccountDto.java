package com.github.mrchcat.front.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record FrontAccountDto(String currencyStringCode,
                              String currencyTitle,
                              BigDecimal balance,
                              boolean isActive) {
}
