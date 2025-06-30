package com.github.mrchcat.accounts.account.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Getter
public class BankCurrency {
    String string_code_iso4217;
    int digital_code_iso4217;
    String ru_name;
}
