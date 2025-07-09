package com.github.mrchcat.front.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record FrontRate(String currencyCode, String title, BigDecimal buyRate, BigDecimal sellRate) {
}
