package com.github.mrchcat.accounts.account.mapper;

import com.github.mrchcat.accounts.account.model.Account;
import com.github.mrchcat.shared.accounts.AccountDto;

import java.util.List;

public class AccountMapper {

    public static AccountDto toDto(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .number(account.getNumber())
                .balance(account.getBalance())
                .currencyStringCode(account.getCurrency().name())
                .build();
    }

    public static List<AccountDto> toDto(List<Account> accounts) {
        return accounts.stream()
                .map(AccountMapper::toDto)
                .toList();
    }
}
