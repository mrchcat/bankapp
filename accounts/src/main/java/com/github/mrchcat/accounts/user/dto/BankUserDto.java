package com.github.mrchcat.accounts.user.dto;

import com.github.mrchcat.accounts.account.dto.AccountDto;
import com.github.mrchcat.accounts.account.model.Account;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record BankUserDto(
        String fullName,
        LocalDate birthDay,
        String email,
        List<AccountDto> accounts
) {
}
