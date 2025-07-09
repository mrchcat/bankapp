package com.github.mrchcat.front.dto;

import java.math.BigDecimal;

public record FrontRate(String currencyCode, String title, BigDecimal buyRate, BigDecimal sellRate) {
}
