package com.github.mrchcat.front.dto;

import com.github.mrchcat.front.model.FrontCurrencies;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
public record FrontAccountDto(String currencyStringCode,
                              String currencyTitle,
                              BigDecimal balance,
                              boolean isActive) {
}
