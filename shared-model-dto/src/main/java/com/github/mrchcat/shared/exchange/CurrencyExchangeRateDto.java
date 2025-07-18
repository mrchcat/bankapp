package com.github.mrchcat.shared.exchange;

import com.github.mrchcat.shared.enums.BankCurrency;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@Setter
@Getter
public class CurrencyExchangeRateDto {
    BankCurrency from;
    BankCurrency to;
    BigDecimal rate;
}
