package com.github.mrchcat.shared.accounts;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
public record BankUserDto(
        UUID id,
        String username,
        String fullName,
        LocalDate birthDay,
        String email,
        List<AccountDto> accounts
) {
}
