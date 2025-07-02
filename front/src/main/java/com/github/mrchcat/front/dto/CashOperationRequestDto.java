package com.github.mrchcat.front.dto;

import com.github.mrchcat.front.model.CashAction;
import com.github.mrchcat.front.model.FrontCurrencies;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CashOperationRequestDto(String username,
                                      BigDecimal value,
                                      FrontCurrencies.BankCurrency currency,
                                      CashAction operation
) {
}
