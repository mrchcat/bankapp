package com.github.mrchcat.front.dto;

import com.github.mrchcat.front.model.BankCurrency;
import com.github.mrchcat.front.model.CashAction;
import com.github.mrchcat.front.model.FrontCurrencies;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CashTransactionRequestDto(String username,
                                        BigDecimal value,
                                        BankCurrency currency,
                                        CashAction action
) {
}
